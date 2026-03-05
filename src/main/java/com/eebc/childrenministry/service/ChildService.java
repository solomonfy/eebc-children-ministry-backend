package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Child;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChildService {
    List<Child> getAllChildren();
    List<Child> getChildrenByFamilyId(String familyId);
    Optional<Optional<Child>> getChildById(String id);
    Optional<Child> getChildByLastName(String last_name);
    Child createChild(Child child);

}
