package com.astro.config;

import com.astro.entity.AdminPanel.FormMaster;
// import com.astro.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.constant.AppConstant;
import com.astro.repository.AdminPanel.FormMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;



@Service
@RequiredArgsConstructor
public class FormCacheService {

    @Autowired
    private  FormMasterRepository formRepository;
    
   

    // private final FormRepository formRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${astro.cache.ttl-seconds}")
    private long ttlSeconds;

    public FormMaster getById(Long id) {
        String key = "formById:" + id;
        FormMaster cached = (FormMaster) redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        FormMaster fromDb = formRepository.findById(id)
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
    public FormMaster save(FormMaster form) {
        FormMaster saved = formRepository.save(form); // proxysql first, always

        redisTemplate.opsForValue().set("formById:" + saved.getFormId(), saved, Duration.ofSeconds(ttlSeconds));
        redisTemplate.opsForValue().set("formByName:" + saved.getFormName(), saved, Duration.ofSeconds(ttlSeconds));
        redisTemplate.delete("allForms");
        redisTemplate.delete("activeForms");

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        FormMaster form = formRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
    new ErrorDetails(
        404,
        AppConstant.ERROR_TYPE_CODE_VALIDATION,
        "NOT_FOUND",
        "Form not found with id: " + id
    )
));
        formRepository.deleteById(id); // proxysql first

        redisTemplate.delete("formById:" + id);
        redisTemplate.delete("formByName:" + form.getFormName());
        redisTemplate.delete("allForms");
        redisTemplate.delete("activeForms");
    }
}