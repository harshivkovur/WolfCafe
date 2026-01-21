package edu.ncsu.csc326.wolfcafe.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * EditUserDto for updating an existing user's profile information.
 * This DTO contains only the editable fields of a user account and is used by 
 * administrators when modifying users. 
 * 
 * @author Sreenidhi Kannan
 */

@Getter
@Setter
public class EditUserDto {
	/** Updated full name of the user */
    private String name;
    
    /** Updated email address of the user */
    private String email;
    
    /** Updated username chosen for user */
    private String username;
}
