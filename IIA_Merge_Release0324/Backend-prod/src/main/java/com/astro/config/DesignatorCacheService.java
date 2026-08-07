package com.astro.config;

import com.astro.entity.AdminPanel.DesignatorMaster;
// import com.astro.exception.NotFoundException;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.constant.AppConstant;
import com.astro.repository.AdminPanel.DesignatorMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DesignatorCacheService {

    @Autowired
    private  DesignatorMasterRepository designatorRepository;

    // private final DesignatorRepository designatorRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${astro.cache.ttl-seconds}")
    private long ttlSeconds;

    public DesignatorMaster getById(Long id) {
        String key = "designatorById:" + id;
        DesignatorMaster cached = (DesignatorMaster) redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        DesignatorMaster fromDb = designatorRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
    new ErrorDetails(
        404,
        AppConstant.ERROR_TYPE_CODE_VALIDATION,
        "NOT_FOUND",
        "Form not found with id: " + id
    )
));
        redisTemplate.opsForValue().set(key, fromDb, Duration.ofSeconds(ttlSeconds));
        return fromDb;
    }

    @Transactional
    public DesignatorMaster save(DesignatorMaster designator) {
        DesignatorMaster saved = designatorRepository.save(designator); // proxysql first

        redisTemplate.opsForValue().set("designatorById:" + saved.getDesignatorId(), saved, Duration.ofSeconds(ttlSeconds));
        redisTemplate.opsForValue().set(
            "designatorByFormAndName:" + saved.getFormId() + ":" + saved.getDesignatorName(),
            saved, Duration.ofSeconds(ttlSeconds));

        redisTemplate.delete("designatorsByFormId:" + saved.getFormId());
        redisTemplate.delete("activeDesignatorsByFormId:" + saved.getFormId());

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        DesignatorMaster designator = designatorRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
    new ErrorDetails(
        404,
        AppConstant.ERROR_TYPE_CODE_VALIDATION,
        "NOT_FOUND",
        "Form not found with id: " + id
    )
));
        designatorRepository.deleteById(id); // proxysql first

        redisTemplate.delete("designatorById:" + id);
        redisTemplate.delete("designatorByFormAndName:" + designator.getFormId() + ":" + designator.getDesignatorName());
        redisTemplate.delete("designatorsByFormId:" + designator.getFormId());
        redisTemplate.delete("activeDesignatorsByFormId:" + designator.getFormId());
    }
}