package org.service.impl;

import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.exeption.NotFoundException;
import org.mapper.RoleDTOMapper;
import org.mapper.impl.RoleDTOMapperImpl;
import org.model.Role;
import org.repository.RoleRepository;
import org.repository.impl.RoleRepositoryImpl;
import org.service.RoleService;

import java.util.List;

public class RoleServiceImpl implements RoleService{
    private RoleRepository roleRepository = RoleRepositoryImpl.getInstance();
    private static RoleService instance;
    private final RoleDTOMapper roleDTOMapper = RoleDTOMapperImpl.getInstance();


    private RoleServiceImpl() {
    }

    public static synchronized RoleService getInstance() {
        if (instance == null) {
            instance = new RoleServiceImpl();
        }
        return instance;
    }

    @Override
    public RoleOutGoingDTO save(RoleIncomingDTO roleDto) {
        Role role = roleDTOMapper.map(roleDto);
        role = roleRepository.save(role);
        return roleDTOMapper.map(role);
    }

    @Override
    public void update(RoleUpdateDTO roleUpdateDto) throws NotFoundException {
        checkRoleExist(roleUpdateDto.getId());
        Role role = roleDTOMapper.map(roleUpdateDto);
        roleRepository.update(role);
    }

    @Override
    public RoleOutGoingDTO findById(Long roleId) throws NotFoundException {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new NotFoundException("Role not found."));
        return roleDTOMapper.map(role);
    }

    @Override
    public List<RoleOutGoingDTO> findAll() {
        List<Role> roleList = roleRepository.findAll();
        return roleDTOMapper.map(roleList);
    }

    @Override
    public boolean delete(Long roleId) throws NotFoundException {
        checkRoleExist(roleId);
        return roleRepository.deleteById(roleId);
    }

    private void checkRoleExist(Long roleId) throws NotFoundException {
        if (!roleRepository.exitsById(roleId)) {
            throw new NotFoundException("Role not found.");
        }
    }
}
