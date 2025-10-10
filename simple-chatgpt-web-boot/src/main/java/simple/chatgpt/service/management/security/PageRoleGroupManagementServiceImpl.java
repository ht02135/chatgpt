package simple.chatgpt.service.management.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.SecurityConfigLoader;
import simple.chatgpt.config.management.security.PageRoleGroupConfig;
import simple.chatgpt.mapper.management.security.PageRoleGroupManagementMapper;
import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class PageRoleGroupManagementServiceImpl implements PageRoleGroupManagementService {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementServiceImpl.class);

    private final PageRoleGroupManagementMapper pageMapper;
    private final UserManagementRoleGroupMappingService userRoleGroupMappingService;
    private final RoleGroupManagementService roleGroupService;
    private final SecurityConfigLoader securityConfigLoader;

    @Autowired
    public PageRoleGroupManagementServiceImpl(
            PageRoleGroupManagementMapper pageMapper,
            UserManagementRoleGroupMappingService userRoleGroupMappingService,
            RoleGroupManagementService roleGroupService,
            SecurityConfigLoader securityConfigLoader) {

        logger.debug("PageRoleGroupManagementServiceImpl START");
        logger.debug("PageRoleGroupManagementServiceImpl pageMapper={}", pageMapper);
        logger.debug("PageRoleGroupManagementServiceImpl userRoleGroupMappingService={}", userRoleGroupMappingService);
        logger.debug("PageRoleGroupManagementServiceImpl roleGroupService={}", roleGroupService);
        logger.debug("PageRoleGroupManagementServiceImpl securityConfigLoader={}", securityConfigLoader);

        this.pageMapper = pageMapper;
        this.userRoleGroupMappingService = userRoleGroupMappingService;
        this.roleGroupService = roleGroupService;
        this.securityConfigLoader = securityConfigLoader;

        logger.debug("PageRoleGroupManagementServiceImpl DONE");
    }

    @PostConstruct
    private void init() {
        logger.debug("init START");
        initializeDB();
        logger.debug("init DONE");
    }

    private void initializeDB() {
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public PageRoleGroupManagementPojo create(PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("create called");
        logger.debug("create pageRoleGroup={}", pageRoleGroup);
        pageMapper.create(pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public PageRoleGroupManagementPojo update(Long id, PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update pageRoleGroup={}", pageRoleGroup);
        pageMapper.update(id, pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public PagedResult<PageRoleGroupManagementPojo> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<PageRoleGroupManagementPojo> items = pageMapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<PageRoleGroupManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public PageRoleGroupManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        PageRoleGroupManagementPojo pageRoleGroup = pageMapper.get(id);
        logger.debug("get return={}", pageRoleGroup);
        return pageRoleGroup;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        pageMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
}
