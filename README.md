# ARCHI-I3

[![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/fr/)
[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)

[architecture_diagram]: architecture_diagram.drawio.png


## Solution Architecture

The solution is implented via a mix of microservices architecture and event driven architecture. Kafka is used as a Broker between the different services:

![diagram][architecture_diagram]


## Installation


\
**Step 1: Clone the GitHub Repository**

1. Open a command line or terminal.

2. Navigate to the directory where you want to clone the project.

3. Run the following command to clone the repository from GitHub:
    ```bash
    git clone https://github.com/yoyo53/archi-I3.git
    ```


\
**Step 2: Configure Environment Variables**

1. In the directory, locate a file named `.env.example`.

2. Duplicate this file and rename the copy to `.env`.

3. Open the `.env` file in a text editor.

4. Fill the fields marked with <> with the according data 


\
**Step 3: Build and run the dockers**

1. Run the following command to build and start the services:
    ```bash
    docker compose up --build
    ```

    The services are now running.


### Test the solution


\
**Step 1: Import routes**

1. Open Postman or Insomnia

2. Navigate through the directory and find the file named "postman-collection.json"

3. Import the json file on your software



\
**Step 2: Test the routes**

1. Navigate through the different routes

2. Here is a flow example:

    - create an Investor and an Agent. Use the createUser route with a body containing either ``AGENT`` or ``INVESTOR`` as role.

    -  Login with your email and password using the login route to receive ``your id``. Remember it

    - As an Agent, ``create a property`` using the ``createProperty`` route. You need to put the ``id of the Agent`` in the ``Authorization`` header. Complete the body as you want.

    - Update it as ``OPENED`` as an Agent using the updateProperty route so that investors can start invest in it. 

    - Add money to your wallet as an Investor using the route ``deposit``. Reference the investor id in the ``Authorization`` header.

    - Invest in a property as an investor using the ``createInvestment`` route. When the investments for the property will reach its price, the property will be marked as ``FUNDED``.

    - Certificates will be then ``delivered``. You can check it using the ``getCertificates`` route. Investors will start receive money.

    - Use the Time routes to ``skip days`` or to set a ``specitic date``.

    - Check user balances using the ``getMyWallet`` route and by specifying the id in the ``Authorization`` header. You will be able to monitor the rental income.

    - Monitor the logs of the ``notification`` service to see the notifications send to users at various steps


