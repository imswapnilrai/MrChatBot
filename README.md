MrChatbot - Android Chat Application

MrChatbot is a basic Android chatting application that leverages Firebase Firestore and Firebase Cloud Storage to provide real-time messaging capabilities. With MrChatbot, users can send and receive messages, 
set their username and profile picture, search for other users by their registered username, and create accounts or sign in using their mobile number and OTP (One-Time Password).

Features:
Real-time Messaging: MrChatbot allows users to send and receive messages in real-time, offering a seamless chat experience.
User Authentication: Users can create accounts or sign in using their mobile numbers and OTP. Additionally, they can set their usernames and profile pictures.
User Search: The app provides a user search functionality, enabling users to find others by their registered usernames.
Firebase Firestore: Firebase Firestore securely stores chat messages and user data in the cloud, ensuring data consistency and synchronization across devices.
Firebase Cloud Storage: User profile pictures are stored in Firebase Cloud Storage, simplifying the management of user profile images.


Usage:
Account Creation and Sign-In:
To create an account, enter your mobile number.
You will receive an OTP (One-Time Password) via SMS for verification.
Set your username and upload a profile picture.
Once registered, you can sign in using your mobile number and OTP.

Chatting:
Navigate to the chat interface.
Select a user to start a conversation.
Send and receive messages in real-time.

User Search:
Utilize the search feature to discover other users by their registered usernames.


Dependencies:
Firebase Firestore: Firestore stores chat messages and user data.
Firebase Authentication: Authentication handles user registration and sign-in using mobile numbers and OTP.
Firebase Cloud Storage: Cloud Storage manages user profile pictures.
Firebase UI: Firebase UI simplifies authentication flows.
Glide: Glide efficiently loads and caches images.
