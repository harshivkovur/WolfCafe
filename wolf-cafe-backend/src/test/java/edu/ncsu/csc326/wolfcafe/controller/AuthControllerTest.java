package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;

/**
 * Tests the authorization controller.
 *
 * @author Sreenidhi Kannan (coverage)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    /** Admin password from application.properties */
    @Value ( "${app.admin-user-password}" )
    private String       adminUserPassword;

    /** Mocked MVC for testing */
    @Autowired
    private MockMvc      mvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Tests logging in as an admin user.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );
    }

    /**
     * Tests creating a customer user and logging in.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }

    /**
     * Additional coverage for AuthController endpoints (delete user).
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testDeleteUserAsAdmin () throws Exception {
        // Register a new user
        final RegisterDto registerDto = new RegisterDto( "Temp User", "tempuser", "temp@example.com", "password123",
                "password123" );
        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() );

        // Fetch the actual user ID from the database
        final Long userId = jdbcTemplate.queryForObject( "SELECT id FROM users WHERE username = ?", Long.class,
                "tempuser" );

        // Perform delete
        mvc.perform( delete( "/api/auth/user/delete/{id}", userId ) ).andExpect( status().isOk() )
                .andExpect( content().string( "User deleted successfully." ) );
    }

    /**
     * Tests that deleting a user fails when authenticated as a non-admin.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testDeleteUserWithoutAdminRole () throws Exception {
        // Register a new user
        final RegisterDto registerDto = new RegisterDto( "Temp User2", "tempuser2", "temp2@example.com", "password123",
                "password123" );
        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() );

        // Fetch the actual user ID from the database
        final Long userId = jdbcTemplate.queryForObject( "SELECT id FROM users WHERE username = ?", Long.class,
                "tempuser2" );

        // Attempt delete as STAFF (non-admin)
        mvc.perform( delete( "/api/auth/user/delete/{id}", userId ) ).andExpect( status().isForbidden() ); // 403
        // Forbidden
    }

    /*
     * Tests registering a new staff member (admin access)
     * @throws Expception if error
     */
    @Test
    @Transactional
    public void testRegisterStaff () throws Exception {
        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );

        final String loginResponse = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) ).andReturn().getResponse().getContentAsString();

        final String token = loginResponse.replaceAll( ".*\"accessToken\"\\s*:\\s*\"([^\"]+)\".*", "$1" );

        final RegisterDto staffDto = new RegisterDto( "Staff Member", "smember", "staff.member@example.com",
                "StrongPass123!", "StrongPass123!" );

        mvc.perform( post( "/api/auth/register/staff" ).header( "Authorization", "Bearer " + token )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( staffDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( content().string( "Staff account created successfully." ) );

        mvc.perform( get( "/api/auth/all" ).header( "Authorization", "Bearer " + token )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[1].name" ).value( "Staff Member" ) );

        mvc.perform( get( "/api/auth/staff" ).header( "Authorization", "Bearer " + token )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].name" ).value( "Staff Member" ) );

        mvc.perform( post( "/api/auth/register/staff" ).header( "Authorization", "Bearer " + token )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( staffDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isBadRequest() );

        final LoginDto staffLogin = new LoginDto( "smember", "StrongPass123!" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staffLogin ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );

    }

}
