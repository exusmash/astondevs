package org.service;

import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.exeption.NotFoundException;

import java.util.List;

public interface RoleService {
    RoleOutGoingDTO save(RoleIncomingDTO role);

    void update(RoleUpdateDTO role) throws NotFoundException;

    RoleOutGoingDTO findById(Long roleId) throws NotFoundException;

    List<RoleOutGoingDTO> findAll();

    boolean delete(Long roleId) throws NotFoundException;
}
