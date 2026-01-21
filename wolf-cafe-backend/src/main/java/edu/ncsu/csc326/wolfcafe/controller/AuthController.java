package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.EditUserDto;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 *
 * @author Sreenidhi Kannan
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    private final AuthService    authService;

    /** Repository for retrieving and filtering user accounts */
    private final UserRepository userRepository;

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Registers a new staff member (admin access)
     *
     * @param registerDto
     *            staff registration information
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/register/staff" )
    public ResponseEntity<String> registerStaff ( @RequestBody final RegisterDto registerDto ) {
        System.out.println( "RECEIVED STAFF REGISTER" );
        System.out.println( registerDto.getUsername() );
        System.out.println( registerDto.getName() );
        System.out.println( registerDto.getPassword() );
        System.out.println( registerDto.getConfirmPassword() );
        System.out.println( registerDto.getEmail() );

        final String response = authService.registerStaff( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }

    /**
     * Retrieves all staff users. Requires ADMIN role.
     *
     * @return list of users with ROLE_STAFF
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/staff" )
    public ResponseEntity<List<User>> getAllStaff () {
        final List<User> staff = userRepository.findAll().stream()
                .filter( u -> u.getRoles().stream().anyMatch( r -> r.getName().equals( "ROLE_STAFF" ) ) ).toList();
        return ResponseEntity.ok( staff );
    }

    /**
     * Retrieves full user details by ID (ADMIN access required)
     *
     * @param id
     *            the ID of the user
     * @return the full user object
     */
    @PreAuthorize ( "hasRole('ADMIN') or hasRole('STAFF')" )
    @GetMapping ( "/{id}" )
    public ResponseEntity<User> getUserById ( @PathVariable ( "id" ) final Long id ) {
        return userRepository.findById( id ).map( user -> ResponseEntity.ok( user ) )
                .orElse( ResponseEntity.status( HttpStatus.NOT_FOUND ).build() );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @DeleteMapping ( "/user/delete/{id}" )
    public ResponseEntity<String> deleteUser ( @PathVariable final Long id ) {
        authService.deleteUserById( id );
        return ResponseEntity.ok( "User deleted successfully." );
    }

    /**
     * Updates user profile info such as name, email, or username Accessible
     * only to ADMIN users
     *
     * @param id
     *            the ID of user being updated
     * @param dto
     *            updated user info
     * @return success message after update
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PutMapping ( "/user/update/{id}" )
    public ResponseEntity<String> editUser ( @PathVariable ( "id" ) final Long id,
            @RequestBody final EditUserDto dto ) {
        authService.editUser( id, dto );
        return ResponseEntity.ok( "User updated successfully." );
    }

    /**
     * Gets all users in the system, only accessible to admin users
     *
     * @return a list of the user items
     */
    @GetMapping ( "/all" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<List<User>> getAllUsers () {
        final List<User> users = userRepository.findAll();
        return ResponseEntity.ok( users );
    }

}
