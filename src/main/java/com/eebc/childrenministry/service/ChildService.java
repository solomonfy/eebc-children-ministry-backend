package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.ChildDTO;
import com.eebc.childrenministry.dto.RegisterChildRequest;
import com.eebc.childrenministry.entity.Child;

import java.util.List;
import java.util.Optional;

public interface ChildService {

    List<ChildDTO> getAllChildren();
    List<ChildDTO> getChildrenByFamilyId(String familyId);
    Optional<ChildDTO> getChildById(String id);
    Optional<ChildDTO> getChildByLastName(String lastName);
    ChildDTO createChild(Child child);
    Child updateChild(String id, Child child);

}