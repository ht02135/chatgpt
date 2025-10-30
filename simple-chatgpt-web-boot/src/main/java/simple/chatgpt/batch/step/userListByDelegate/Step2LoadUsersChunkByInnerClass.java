package simple.chatgpt.batch.step.userListByDelegate;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import simple.chatgpt.batch.job.userList.UserListJobConfig;
import simple.chatgpt.batch.step.BatchJobConstants;
import simple.chatgpt.mapper.batch.JobRequestMapper;
import simple.chatgpt.mapper.management.UserManagementMapper;
import simple.chatgpt.pojo.batch.JobRequest;
import simple.chatgpt.pojo.management.UserManagementPojo;

/*
By moving the reader, processor, and writer into private inner classes:
You avoid duplicate bean names entirely because Spring doesn’t need to manage them as separate beans.
Everything stays encapsulated inside the step, which is cleaner and safer.
Logging and JobRequest handling stay exactly the same, so you retain full visibility into each item processed.
Using MyBatisCursorItemReader ensures you’re streaming users efficiently, instead of loading all at once.
*/

@Component
public class Step2LoadUsersChunkByInnerClass extends AbstractJobRequestByDelegateStep {

	private static final Logger logger = LogManager.getLogger(Step2LoadUsersChunkByInnerClass.class);

	private final SqlSessionFactory sqlSessionFactory;

	private StepExecution stepExecution;
	private JobRequest jobRequest;

	public Step2LoadUsersChunkByInnerClass(JobRequestMapper jobRequestMapper, UserManagementMapper userManagementMapper,
			SqlSessionFactory sqlSessionFactory) {
		super(jobRequestMapper, userManagementMapper);
		this.sqlSessionFactory = sqlSessionFactory;
	}

	// =========================================
	// STEP BEAN
	// =========================================
	public Step step2LoadUsersByInnerClass(StepBuilderFactory stepBuilderFactory) {
		logger.debug("step2LoadUsersByInnerClass called");

		return stepBuilderFactory.get("step2LoadUsersByInnerClass").<UserManagementPojo, UserManagementPojo>chunk(50)
				.reader(new UserReader()).processor(new UserProcessor()).writer(new UserWriter()).listener(this)
				.build();
	}

	// =========================================
	// PRIVATE INNER READER
	// =========================================
	private class UserReader extends MyBatisCursorItemReader<UserManagementPojo> {

		public UserReader() {
			setSqlSessionFactory(sqlSessionFactory);
			setQueryId("simple.chatgpt.mapper.management.UserManagementMapper.getAll"); // MyBatis mapper query id
		}

		@Override
		public UserManagementPojo read() throws Exception {
			jobRequest = getOneRecentJobRequestByParams(UserListJobConfig.JOB_NAME, 200, 1,
					JobRequest.STATUS_SUBMITTED);
			logger.debug("read jobRequest={}", jobRequest);

			if (jobRequest == null) {
				logger.debug("No live JobRequest found");
				return null;
			}

			UserManagementPojo user = super.read();
			if (user != null) {
				logger.debug("UserReader returning user id={}, userName={}", user.getId(), user.getUserName());
			}
			return user;
		}
	}

	// =========================================
	// PRIVATE INNER PROCESSOR
	// =========================================
	private class UserProcessor implements ItemProcessor<UserManagementPojo, UserManagementPojo> {
		@Override
        public UserManagementPojo process(UserManagementPojo user) {
			logger.debug("UserProcessor processing user id={}, userName={}", user.getId(), user.getUserName());
			return user;
		}
	}

	// =========================================
	// PRIVATE INNER WRITER
	// =========================================
	private class UserWriter implements ItemWriter<UserManagementPojo> {
		@Override
        public void write(List<? extends UserManagementPojo> users) {
			try {
				List<Long> userIds = new ArrayList<>();
				for (UserManagementPojo user : users) {
					userIds.add(user.getId());
				}
                logger.debug("UserWriter userIds={}", userIds);

				// ==================================================
                // Use helper methods instead of manual stepData & ExecutionContext
				// ==================================================
				List<Long> existingIds = (List<Long>) stepExecution.getJobExecution().getExecutionContext()
						.get(BatchJobConstants.CONTEXT_USER_IDS);
                if (existingIds == null) existingIds = new ArrayList<>();
				existingIds.addAll(userIds);
                logger.debug("UserWriter existingIds={}", existingIds);

				updateJobRequestStepData(jobRequest, stepExecution, BatchJobConstants.CONTEXT_USER_IDS, existingIds);
				updateJobRequest(jobRequest, 300, 1, JobRequest.STATUS_SUBMITTED);

			} catch (Exception e) {
				logger.error("Error e={}", e);
                updateJobRequest(jobRequest, jobRequest.getProcessingStage(), 999, 
                	JobRequest.STATUS_FAILED, e.getMessage());
				throw e;
			}
		}
	}

	// =========================================
	// STEP LISTENER
	// =========================================
    @Override
	public void beforeStep(StepExecution stepExecution) {
    	logger.debug("beforeStep called");
        logger.debug("beforeStep stepExecution={}", stepExecution);
        
		this.stepExecution = stepExecution;
	}

    @Override
	public ExitStatus afterStep(StepExecution stepExecution) {
    	logger.debug("afterStep called");
        logger.debug("afterStep stepExecution={}", stepExecution);
        
		this.stepExecution = null;
		return stepExecution.getExitStatus();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		return RepeatStatus.FINISHED;
	}
}
