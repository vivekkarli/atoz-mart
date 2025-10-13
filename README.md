
# AtoZ mart

AtoZ mart is a e-commerce shopping application, which allows users to shop items, log in, sign up, add to cart, place an order and much more


### run the project using docker
prerequisites: docker desktop, postman

**Step 1:** download the docker-compose.yml file  
**Step 2:** go to docker compose terminal and execute below command
```
docker compose up -d
```
wait for few mins (10 mins) untill all services status is healthy  
<screeen shot>  
if you face memory issues in docker, download the lite version of docker-compose from this repo.  
``
./docker/lite/docker-compose.yml  
``

**step 3)** download the `atozmart.postman_collection.json` file. Import the collection into postman.

### Overview of the microservices

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

**12. wishlist-service**
- wishlist service is used to wishlist an item for quicker access. User can add, view, delete items from wishlist.

