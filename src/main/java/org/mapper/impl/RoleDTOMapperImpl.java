package org.mapper.impl;

import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.mapper.RoleDTOMapper;
import org.model.Role;

import java.util.List;

public class RoleDTOMapperImpl implements RoleDTOMapper {
    private static RoleDTOMapper instance;

    private RoleDTOMapperImpl() {
    }

    public static synchronized RoleDTOMapper getInstance() {
        if (instance == null) {
            instance = new RoleDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Role map(RoleIncomingDTO roleIncomingDto) {
        return new Role(
                null,
                roleIncomingDto.getName()
        );
    }

    @Override
    public Role map(RoleUpdateDTO roleUpdateDto) {
        return new Role(
                roleUpdateDto.getId(),
                roleUpdateDto.getName());
    }

    @Override
    public RoleOutGoingDTO map(Role role) {
        return new RoleOutGoingDTO(
                role.getId(),
                role.getName()
        );
    }

    @Override
    public List<RoleOutGoingDTO> map(List<Role> roleList) {
        return roleList.stream().map(this::map).toList();
    }
}
