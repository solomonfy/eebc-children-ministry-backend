package com.eebc.childrenministry.audit;

import com.eebc.childrenministry.repository.AuditHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * JPA EntityListeners are not managed Spring beans — they're instantiated by JPA directly.
 * This component bridges the gap by injecting Spring beans into the static listener
 * after the application context is fully initialized.
 */
@Component
public class SpringBeanProvider implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AuditHistoryRepository auditHistoryRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AuditableEntityListener.setAuditRepo(auditHistoryRepository);
        AuditableEntityListener.setApplicationContext(applicationContext);
    }
}
