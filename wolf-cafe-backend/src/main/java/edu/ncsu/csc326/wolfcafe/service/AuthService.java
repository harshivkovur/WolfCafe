package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.EditUserDto;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;

/**
 * Authorization service
 * @author Sreenidhi Kannan
 */
public interface AuthService {
    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Registers a new staff member (with admin access)
     *
     * @param registerDto
     *            staff registration information
     * @return message for success or failure
     */
    String registerStaff ( RegisterDto registerDto );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Gets the name of the user with the given id
     *
     * @param id
     *            id of user to delete
     */
    String getNameById ( Long id );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserById(Long id);
    
    /**
     * Updates info of existing user
     * @param id ID of user to edit
     * @param dto DTO containing updated user info
     */
    String editUser(Long id, EditUserDto dto);
}
