# WolfCafe

## Extra Credit
For our extra credit, we chose 3 options. We chose to do anonymous orders, order history, and user interface enhancements

### Anonymous Orders
When not logged in under a user account, clients can see the list of current account-less orders that were placed the same day. At the top of this page is a "Create Order" functionality that works similarly to how it works when logged in, but stores "null" as the user's ID. They can cancel it, or pick it up if the order has been fulfilled. On the staff end, these users just show up as "Guest".

### Order History
When logged in as a staff or admin user, the order screen has a search field to sort by specific days of the month. When the page is originally navigated to, it displays today's date and orders. At the top of the page, it displays the total revenue (all orders are included besides cancelled orders). By selecting a new page from the search field is automatically updates the results on the page. Each page is sorted in descending from most recent to least recent. 

### User Interface Enhancements
For our UI enhancements, we tackled a plethora of items. The primary focus was on creating new site themes, listed in the thenes.css file. We have a Light, Dark, School Spirit, and Vaporwave theme. Each theme updates almost every aspect of the site visuals, with the exception being nav-bar consistency between the Light and Dark skins. Our Light and Dark Skins are primarily based around the original color palettes- white, varying shades of dark grays. The vaporwave color palette began using this palette of colors- https://www.color-hex.com/color-palette/94934 but was slightly shifted to be more comfortable for viewers. Finally, our school spirit colors stem from the colors listed here- https://brand.ncsu.edu/designing-for-nc-state/color/.

**NavBars**
For the navigation bars at the top of the pages, we chose to have left-aligned links for pages a user might want to do (such as orders, the inventory list). Auth functionalities (login, register, theme changes) are on the right side of the page to differentiate their purposes. When an item is Active on the navbar, the color is bolded and a stand-out different color from the other tabs. In Light/Dark skins, the active tab is bolded white compared to gray while the background color remains consistent from the original iteration.. In school spirit, hovered text is bolded black compared to white and is on a WolfPack Red background. In Vaporwave, it is bolded dark purple on a cyan background.

In addition, non-active tabs that are hovered will change colors so the user can clearly tell what they are clicking. Light and Dark these hovered tabs turn WolfPack Red, #CC0000. The School Spirit hovered tabs turn black, and then vaporwave turns dark purple. The "WolfCafe" logo is always bolded white, but will also change colors when hovered.

**Backgrounds**
For our Light and School Spirit skins, the overall background stays white. Dark skin changes it to dark gray, and vaporwave is a slightly blue off-white. 

**Cards**
Our "Card" items, such as our inventory list and our login screen, are differentiated from the back through use of a colored border and potentially changing the color. Our Light, Dark, and School Spirit themes have a WolfPack red border, and our Vaporwave theme uses dark purple. Our Light and School Spirit themes match the background color in the card. Our Dark theme copies the background color of our nav bar for homogenity, and our vaporwave uses a softer purple color to add a splash more color without feeling overwhelming.

**Buttons**
Our buttons change colors when highlighted to indicate to users what they are hovering. Below is the list of themes with the button changes.

Light Theme
Not hovered: Soft gray background, darker gray border, black text.
Hovered: WolfPack red background, no border, white text

Dark Theme
Not hovered: Dark gray background, light gray border, white text.
Hovered: WolfPack red background, no border, white text

School Spirit Theme
Not hovered: White background, WolfPack red border, Wolfpack Red text.
Hovered: Black background, WolfPack red border, white text.

Vaporwave Theme
Not hovered: Dark purple background, no border, white text
Hovered: Cyan background, bold dark purple border, white text. 

**Modals**
We have various confirmation screens to notify a user when things are done (such as order payment) or confirm chnges to an order's status. These modals utilize the standard buttons for the skins. The Light, School Spirit, and Vaporwave themes all use a white background. The Dark theme uses a slightly lighter gray that matches our table headers. The Light, Dark, and School Spirit modals all use a red border. The Vaporwave uses a light purple border. The modals also block doing anything until they are cleared, whether by confirming or rejecting the action. 

**Tables**
All of our tables have a solid border to differentiate the header of the table from the rest of the table.  

Light Theme
Row backgrounds: White, light gray
Text color: Black
Header Text color: Black
Header background: light gray
Header bottom border: WolfPack Red
Overall borders: gray

Dark Theme
Row backgrounds: Very dark gray, dark gray
Font color: White
Header text color: White
Header background: Dark gray
Header bottom border: WolfPack Red
Overall borders: medium gray

School Spirit Theme
Row backgrounds: White, light gray
Text color: Black
Header text color: White
Header background: Wolfpack Red
Header bottom border: Black
Overall borders: Wolfpack Red

Vaporwave Theme
Row backgrounds: White, pale lavendar
Text color: Black
Header text color: Dark purple
Header background: Light purple
Header bottom border: Dark purple
Overall borders: Light blue

## Install Lombok
Lombok is a library that lets us use annotations to automatically generate getters, setters, and constructors.  For Lombok to work in Eclipse (and other IDEs like IntelliJ or VS Code), you need to set up Lombok with the IDE in addition to including in the pom.xml file.

Follow the [instructions for setting up Lombok in Eclipse](https://projectlombok.org/setup/eclipse).  Make sure you download the laste version of Lombok from [Maven Repository](https://mvnrepository.com/artifact/org.projectlombok/lombok) as a jar file.

## Configuration

Update `application.properties` in `src/main/resources/` and `src/test/resources/`.

  * Set `spring.datasource.password` to your local MySQL password`
  * Set `app.jwt-secret` as described below.
  * Set `app.admin-user-password` to a plain text string that you will use as the admin passw ord.
  
### Set `app.jwt-secret`

We will create a secret key that will be used for JWT authentication.  Think of a secret key phrase.  You'll want to encrypt it using SHA256 encryption.  You can use a tool like:  https://emn178.github.io/online-tools/sha256.html to generate the encrypted text.  Copy that into your `application.properties` file.

## Setup
The rest of the setup for WolfCafe is the same as for [CoffeeMaker](https://pages.github.ncsu.edu/engr-csc326-staff/326-course-page/onboarding/setup).


## User Roles

User roles are defined and initialized in `config.Roles`.  The `ADMIN` role is a constant.  All other roles are listed in the `UserRoles` enumeration. You can add new roles by adding the role name to the enumeration.

### Initializing the Roles/Admin user in the DB

`config.SetupDataLoader` initializes the DB with roles and creates a default user with the `ADMIN` role.  This class is automatically run when the application starts.  

The admin user has the user name of "admin" and an email address of "admin@admin.edu".  You will specify the password for the admin user in the `application.properties` file.  The password is read in from the `application.properties` and then encrypted using the password encoder.  

## Testing User Authentication in Postman

The following provides examples of how to work with user authentication in Postman.

### Create a New User

Endpoint: `POST http://localhost:8080/api/auth/register`

Body:

```
{
    "name": "Sarah Heckman",
    "username": "sheckman",
    "email": "sheckman@ncsu.edu",
    "password": "sarah"
}
```

Response: 201 Created

```
User registered successfully.
```

### Login with User

Endpoint: `POST http://localhost:8080/api/auth/login`

Body: 

```
{
    "usernameOrEmail": "sheckman",
    "password": "sarah"
}
```

Response: 200 OK

```
{
    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzaGVja21hbiIsImlhdCI6MTcyOTEyNjg1MiwiZXhwIjoxNzI5NzMxNjUyfQ.WiPROZAMhNbiB8H3fhNJdiC-XX5RJEcHXzmGPEH7aMEFvsjbsvk2m1ZcAKi-lTdt",
    "tokenType": "Bearer",
    "role": "ROLE_CUSTOMER"
}
```

Note the accessToken will vary with each login.  You'll want to save this for testing endpoints that require authentication.

### Get Items

Roles: STAFF, CUSTOMER

Authorization:
  * Bearer
  * Token - copy from the response of an authenticated User
  
Response: 200 OK

```
JSON list of items
```
