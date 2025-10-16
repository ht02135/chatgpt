package simple.chatgpt.service.management.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.security.PageManagementMapper;
import simple.chatgpt.pojo.management.security.PageManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class PageManagementServiceImpl implements PageManagementService {

    private static final Logger logger = LogManager.getLogger(PageManagementServiceImpl.class);

    private final PageManagementMapper pageMapper;
    private final Validator validator;

    @Autowired
    public PageManagementServiceImpl(PageManagementMapper pageMapper) {
        logger.debug("PageManagementServiceImpl constructor START");
        logger.debug("pageMapper={}", pageMapper);

        this.pageMapper = pageMapper;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        logger.debug("PageManagementServiceImpl constructor DONE");
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("postConstruct START");
        initializeDB();
        logger.debug("postConstruct DONE");
    }

    public void initializeDB() {
        logger.debug("initializeDB called");
        /*
         hung: intentionally empty
         */
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public PageManagementPojo create(PageManagementPojo page) {
        logger.debug("create called");
        logger.debug("create page={}", page);

        pageMapper.create(page);
        return page;
    }

    @Override
    public PageManagementPojo update(Long id, PageManagementPojo page) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update page={}", page);

        pageMapper.update(id, page);
        return page;
    }

    @Override
    public PagedResult<PageManagementPojo> search(Map<String, String> params) {
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

        Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));

        List<PageManagementPojo> items = pageMapper.search(mapperParams);
        long totalCount = items.size();

        PagedResult<PageManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public PageManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        PageManagementPojo page = pageMapper.get(id);
        logger.debug("get return page={}", page);
        return page;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        pageMapper.delete(id);
    }

    // ==============================================================
    // ==================== OTHER METHODS ===========================
    // ==============================================================

    @Override
    public List<PageManagementPojo> getPageByParams(Map<String, Object> params) {
        logger.debug("getPageByParams called");
        logger.debug("getPageByParams params={}", params);

        List<PageManagementPojo> pages = pageMapper.search(params);
        logger.debug("getPageByParams pages={}", pages);
        return pages;
    }

    @Override
    public List<PageManagementPojo> getAll() {
        logger.debug("getAll called");

        Map<String, Object> params = new HashMap<>();
        List<PageManagementPojo> pages = getPageByParams(params);
        logger.debug("getAll pages={}", pages);
        return pages;
    }

    @Override
    public PageManagementPojo getPageByUrlPattern(String urlPattern) {
        logger.debug("getPageByUrlPattern called");
        logger.debug("getPageByUrlPattern urlPattern={}", urlPattern);

        Map<String, Object> params = new HashMap<>();
        params.put("urlPattern", urlPattern);

        List<PageManagementPojo> pages = getPageByParams(params);

        if (pages == null || pages.isEmpty()) {
            logger.debug("getPageByUrlPattern found nothing for urlPattern={}", urlPattern);
            return null;
        }

        PageManagementPojo page = pages.get(0);
        logger.debug("getPageByUrlPattern returning page={}", page);
        return page;
    }
}
