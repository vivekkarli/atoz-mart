# AtoZ mart

AtoZ mart is a e-commerce shopping application, which allows users to shop items, log in, sign up, add to cart, place an order and much more

[Run the project using Docker](#run-the-project-using-docker)<br/>
[Overview of all microservices](#overview-of-the-microservices)<br/>
[Project overview using Postman](#project-overview-using-postman)<br/>
[Project overview using Frontend]<br/>

## Run the project using Docker
prerequisites: Docker desktop, Postman

**Step 1:** download the docker-compose.yml file  
**Step 2:** go to docker compose terminal and execute below command
```
docker compose up -d
```
wait for few mins (10 mins) untill all services status is healthy<br/>
<br/>
<img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/c90fa788-ad2a-41a7-9ef7-4c4167d1031f" />
<br/>
 
if you face memory issues in docker, download the lite version of docker-compose from this repo.  
``
./docker/lite/docker-compose.yml  
``

**Step 3:** download the `atozmart.postman_collection.json` file. Import the collection into postman.

## Overview of the microservices

**1. atozmart-authserver**
- authserver is reponsible for authentication and authorization, issues and validates JWT tokens, maintains users basic information securely.  
- user can sign-up, login, change passwords, handles forgot password flows etc.User details are **cached to redis**, to faster validation. And cache will get updated when there are any changes to the user details.
- Gateway server uses it for validating jwt tokens.

**2. atozmart-gatewayserver**
- its the main entry point to the underlying microservices. All requests pass throught the gatewayserver.
- Gatewayserver itercepts every http requests and performs necessary checks such as 
  - authentication (using authserver or keycloak), role based authorization.
  - performs url routing
  - protocal conversion https -> http
  - add custom headers (X-Username, X-User-Email)
  - implements circuit breakers

**3. catalog-service**
- catalog-service is responsible to maintain the catalog items, serve items & images to the user, add and update items to the inventory
The images are served from **AWS S3** service and these images are cached to avoid repeated network calls to S3.

**4. cart-service**
- cart service is responsible to maintain the items in the cart and place an order. Users can add items to the cart, can change the quantity, apply coupons etc, calls order service to place the order.
- items in the cart are deleted as soon as placing the order

**5. order-service**
- order service is the internal service which is used to place an order, store order details, generate order-id, send an email notification to the user.
- Admins or agents can modify orders (yet to be implemented)
- users can view their past orders, cancel their orders.

**6. notification-service**
- notification service is also an internal service which is used to send email notifications to the users, 
- comes into picture to send emails when the signs up, to send password reset links, email verification links, order acknowledgemts etc.
- Services send asynchronous email requests to notification service via a message broker (rabbitMQ in this case).
- It's build on spring-cloud-functions which makes use of java functional interfaces

**7. profile-service**
- profile service is responsible for showing users profile details. When user signs up a basic profile is being created.
- User can furthur add more profile details such as addresses
- User can edit their profile details
- User can upload his/her profile photo

**8. Keycloak**
- Keycloak plays an important role in allowing user to log in with Keycloak credentials, mimicing the "sign-in with google"
- pre-configured with OAuth2 authorization grant type flow to ease the logging experience.

**9. Redis**
- Redis is used as cache datastore for storing frequently accessed data, which helps reduce response time and avoid frequent database calls and network calls.

**10. RabbitMQ**
- RabbitMQ is mainly used for asynchronous communication between microservices
- main funtions are:
  - send email notifications. diff services -> notification-service
  - create basic profile when user signs up. authserver -> profile-service
  - update the stock, order-service -> catalog service

**11. Grafana**
- Grafana is used to visualize the telemetry data collected from all the services
- loki -> collects logs
- prometeus -> collects metrics
- tempo -> for distributed tracing

**12. atozmart-configserver**
- atozmart-configserver is the centralized configuration server
- refers to repo's atozmart-config folder to resolve all the clients configuration based on application name.

**13. eurekasever**
- eurekasever acts as service registry allowing the clients to service descovery
- all the services acts as the clients and registers multiple instances of themselves into service registry
- clients after getting information of running instances of other clients, then performs client-side load balancing to communicate with a particular instance
  
**14. wishlist-service**
- wishlist service is used to wishlist an item for quicker access. User can add, view, delete items from wishlist.

## Project overview using Postman

download `atozmart.postman_collection.json` file. Import the collection into postman.
Make sure all the services are up and running in docker (refer: [run the project using docker](#run-the-project-using-docker))\
Go to gateway folder, you can see all the services and their APIs.<br/>
Test if gateway server is up and running<br/>
<br/>
<img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/0d6b6809-2104-4984-b59d-e72833933d65" />
<br/>
### explore authserver
1. **Sign up** by giving username, password, email and other details. Upon successfull signup we get 202 accepted status.
   - password will be hashed and stored into the database securely.
   <br/>
   <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/3e5b0a5c-dc55-4d88-8f7c-3ccac2b5f171" /></p>
   <br/>
3. **Log in** by same username and password, we get access token (JWT token) in the headers with 30 mins of validatiy.
   <br/>
   <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/e02082fa-5d17-4487-bf6b-c4230cedbac5" /></p>
   <br/>
   - This access token should be passed to all API calls in Authorization header.
   - For simplicity this access token will be passed automatically to all APIs in Postman in the form of env variable, set by post script.
     <br/>
4. **Forgot Password** and **Reset Password**- These APIs are used when user forgets his/her password.
   - A reset link with unique token will be sent to users registered email id. This reset link is bound to expiry.
   - User/frontend application then calls the reset-password API to update the new credentials.
   - User Flow:
     - to send reset link enter username
        <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/191597f9-b149-49d9-9c91-64e2a43dce1d" /></p>
     - email notification
        <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/22bd868a-e3da-4219-a0fb-d534f356f649" /></p>
        <br/>
     - click on reset-password link and copy the token from the url and paste in the reset-password API
        ex: `http://localhost:3000/reset-password?token=ZDkwZjMyMzgtOWFiYi00YzYzLWI0YTQtOGRlMjYzMGZjYTEw`
     - provide new password and then submit. User can now login with new password.
        <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/6d22d2b9-5b58-4ce9-bc43-4102f22a0ab8" /></p>
        <br/>
5. **Authorize** endpoint is used by gateway server to authorize the JWT tokens. Requires previlaged access.
   <br/>
   <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/c00bfbc5-43a0-4f14-ad77-83e07ee3cc91" /></p>
   <br/>
6. explore other APIs like change-password, verify-email & confirm-email etc.

### explore catalog-service
- All GET endpoints in catalog-service doesn't require any authentication
1. **get all items page filters**- returns items based on catagories and pagination parameters
    <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/79d455c9-8f31-42a5-af0f-4ac17346c978" /></p>
2. **upload image**- to upload item images to S3, requires previlaged access. Consumes multipart form data
  - attach the image of the item (key: file). Specify item id in the path params. Submit the request.
  - upon succesfull image upload, we get 201 created status
    <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/a4a6ee99-ccd5-4f18-a726-ab654046e218" /></p>
  - Creates a new bucket (atozmart-bucket), by using IAM user credentials.
    <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/ce55427e-e50a-47be-a0c1-06ecb7e6f94b" /></p>
3. **get image** API used to view the image, This API is consumed by frontend to render the item image. The image is cached to redis with a ttl of 6 mins, to avoid repeated http calls to S3
    <p align="left"><img width="700" height="300" alt="image" src="https://github.com/user-attachments/assets/61ab3a8c-3afc-4615-ab98-7c231e5e9f32" /></p>
   




