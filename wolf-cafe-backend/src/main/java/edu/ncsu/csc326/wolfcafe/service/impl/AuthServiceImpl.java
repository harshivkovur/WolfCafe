package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.EditUserDto;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 * @author Sreenidhi Kannan
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** User repository */
    private final UserRepository        userRepository;
    /** Role repository */
    private final RoleRepository        roleRepository;
    /** Password encoder object */
    private final PasswordEncoder       passwordEncoder;
    /** Authentication manager */
    private final AuthenticationManager authenticationManager;
    /** JWT Token provider for working with user tokens */
    private final JwtTokenProvider      jwtTokenProvider;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );
        return "User registered successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        // Set user ID and username
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            jwtAuthResponse.setId( loggedInUser.getId() ); // <-- add ID
            jwtAuthResponse.setUsername( loggedInUser.getUsername() ); // <--
                                                                       // add
                                                                       // username
        }

        return jwtAuthResponse;
    }

    @Override
    public String registerStaff ( final RegisterDto registerDto ) {
        if ( registerDto.getName() == null || registerDto.getName().trim().isEmpty() ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Name cannot be empty." );
        }

        if ( registerDto.getEmail() == null || registerDto.getEmail().trim().isEmpty()
                || !registerDto.getEmail().matches( "^[A-Za-z0-9+_.-]+@(.+)$" ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Invalid email format." );
        }

        if ( registerDto.getUsername() == null || registerDto.getUsername().trim().isEmpty() ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username cannot be empty." );
        }

        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }

        if ( registerDto.getPassword() == null || registerDto.getPassword().trim().isEmpty() ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Password cannot be empty." );
        }

        if ( registerDto.getConfirmPassword() == null
                || !registerDto.getPassword().equals( registerDto.getConfirmPassword() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Passwords do not match." );
        }

        final User staffUser = new User();
        staffUser.setName( registerDto.getName() );
        staffUser.setUsername( registerDto.getUsername() );
        staffUser.setEmail( registerDto.getEmail() );
        staffUser.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Role staffRole = roleRepository.findByName( "ROLE_STAFF" );
        if ( staffRole == null ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Staff role not found in database." );
        }

        final Set<Role> roles = new HashSet<>();
        roles.add( staffRole );
        staffUser.setRoles( roles );

        userRepository.save( staffUser );

        return "Staff account created successfully.";

    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    @Override
    public void deleteUserById ( final Long id ) {
        userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );
        userRepository.deleteById( id );
    }

    @Override
    public String getNameById ( final Long id ) {
        try {
            final User user = userRepository.findById( id ).get();
            return user.getName();
        }
        catch ( final Exception e ) {
            return "Anonymous";
        }

    }
    
    /**
     * Updates info of existing user
     * @param id ID of user to edit
     * @param dto DTO containing updated user info
     * @throws ResourceNotFoundException if resource not found
     */
    @Override
    public String editUser(Long id, EditUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        userRepository.save(user);
        
        return "User updated successfully.";
    }

}
