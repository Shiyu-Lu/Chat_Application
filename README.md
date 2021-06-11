# Atoms - A Web-Based Chat Application

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- TABLE OF CONTENTS -->
<!-- <details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details> -->

<!-- ABOUT THE PROJECT -->
## About The Project
  
This is a final project for the course *Java Programming* of Peking University delivered by *Dashi Tang*. In the project, we are required to freely develop a program based on Java language.  

Therefore we built this Vue.js chat application with Java backend. And it's called *Atoms* because we want to get people connected through this web-based app. Hope you'll enjoy it! :)

![Sign In Page Screenshot][signin-screenshot]

### Built With

- Java
- HTTP Server
- HTML
- CSS
- Vue.js
- NPM
- SQLite3

<!-- DESCRIPTION -->
## Description

### Features
- Create new accounts
- Sign in
- Add contacts
- Search contacts
- Delete contacts
- Chat with contacts
- Browse history messages

### UI Design and User Flow

![UI Design][ui-design]
![User Flow][user-flow]

**Feature Highlights**
- Login Page:
  - Set quick access to *SignUp Page*
- SignUp Page: for creating new accounts
  - Customize the validation rules for the two-factor password verification
- Navigation Menu: a common component of *Chats Page* and *Contacts Page*
  - Set distinct color for the buttons of different pages with active status
- Chats Page: contacts and history messages
  - Scroll to browse all contacts and the last conversation message
  - Arrange the contacts in reverse order of the timestamp of the last message 
  - Set distinct background color for the contact with mouse hovering over it
  - Scroll to browse the related history messages immediately after clicking one contact on the left
- Contacts Page: manage the contacts
  - add or delete contacts

### Front End Structure

```
└── src
    ├── assets
    │   ├── css
    │   │   ├── home.css
    │   │   ├── menu.css
    │   │   ├── contacts.css
    │   │   ├── styles.css
    │   ├── background.jpg 
    ├── components
    │   ├── common
    │   │   ├── NavMenu.vue
    │   ├── chats   
    │   │   ├── ChatsIndex.vue
    │   │   ├── SingleMsgCard.vue
    │   │   ├── SingleMsgStream.vue
    │   ├── contacts 
    │   │   ├── ContactsIndex.vue
    │   │   ├── SingleContact.vue
    │   ├── Home.vue
    │   ├── Login.vue
    │   ├── SignUp.vue 
    ├── router
    │   ├── index.js
    ├── store 
    │   ├── index.js 
    |── App.js
    |── main.js
```

- ```assets``` - some images and static ```.css``` files for different components
- ```components``` - major part of the frontend construction
  - ```common``` - common navigation menu for home pages
  - ```chats``` - components of *Chats Page*, including ```ChatsIndex.vue``` for main page, ```SingleMsgCard.vue``` to load the contacts, and ```SingleMsgStream.vue``` to load the history messages
  - ```contacts``` - components of *Contacts Page*, including ```ContactsIndex.vue``` for main page and ```SingleContact.vue``` to load all contacts
  - Other files -  ```Login.vue``` for *Login Page* and ```SignUp.vue``` for *SignUp Page*
- ```router``` - map the components to the routes
- ```store``` - some static variables

### Back End Structure

```
└── src
    ├── BLL
    │   ├── ChatServer.java
    │   ├── ChatHttpServer.java 
    │   ├── ChatClientForUser.java 
    │   ├── ChatClientForServer.java 
    │   ├── ServerThread.java 
    ├── DAL
    │   ├── Account.java
    │   ├── AccountFriendDAO.java
    │   ├── ClientDBConnection.java
    │   ├── Conversation.java
    │   ├── ConversationAccountDAO.java
    │   ├── ConversationClientDAO.java
    │   ├── ConversationMessage.java
    │   ├── ConversationServerDAO.java
    │   ├── LocalMessageDAO.java
    │   ├── OfflineMessageDAO.java
    │   ├── RawAccountRecordDAO.java
    │   ├── ServerDBConnection.java
    ├── Service
    │   ├── ChatClientForServerService.java
    │   ├── ChatClientForUserService.java
    │   ├── ServerThreadService.java
    ├── model 
    │   ├── CreateAccountRequestMessage.java
    │   ├── FriendConversationRecord.java
    │   ├── GeneralMessage.java
    │   ├── LoginRequestMessage.java
    │   ├── NormalMessage.java
    │   ├── OfflineMessageRecord.java
    │   ├── RawAccountRecord.java
    │   ├── RawConversationRecord.java
```

### Database Construction

![Database ERD][db-erd]


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Shiyu-Lu/Chat_Application?color=brightgreen&style=for-the-badge
[contributors-url]: https://github.com/Shiyu-Lu/Chat_Application/graphs/contributors
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/shiyu-lu-84314b190
[signin-screenshot]: screenshots/SignIn-Page.png
[user-flow]: screenshots/UserFlowChart.png
[ui-design]: screenshots/UIDesign.png
[db-erd]: screenshots/ERD.png
