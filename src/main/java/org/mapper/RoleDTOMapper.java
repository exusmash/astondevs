package org.mapper;

import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.model.Role;

import java.util.List;

public interface RoleDTOMapper {
    Role map(RoleIncomingDTO roleIncomingDTO);

    Role map(RoleUpdateDTO roleUpdateDTO);

    RoleOutGoingDTO map(Role role);

    List<RoleOutGoingDTO> map(List<Role> roleList);
}
