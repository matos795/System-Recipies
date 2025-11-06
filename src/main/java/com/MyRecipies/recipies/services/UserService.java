package com.MyRecipies.recipies.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.MyRecipies.recipies.dto.UserDTO;
import com.MyRecipies.recipies.dto.UserInsertDTO;
import com.MyRecipies.recipies.entities.Role;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.projections.UserDetailsProjection;
import com.MyRecipies.recipies.repositories.RoleRepository;
import com.MyRecipies.recipies.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository repository;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
	private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> userList = repository.searchUserAndRolesByEmail(username);
        if(userList.size() == 0){
            throw new UsernameNotFoundException("User not found");
        }
        User user = new User();
        user.setEmail(username);
        user.setPassword(userList.get(0).getPassword());
        for (UserDetailsProjection details : userList) {
            user.addRole(new Role(details.getRoleId(), details.getAuthority()));
        }

        return user;
    }

    public UserDTO insert(UserInsertDTO dto) {
        User user = new User();
        dtoToEntity(user, dto);
        user = repository.save(user);
        return new UserDTO(user);
    }

    private void dtoToEntity(User entity, UserInsertDTO dto){
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBirthDate(dto.getBirthDate());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.addRole(roleRepository.findByAuthority("ROLE_CLIENT"));
    }
}
