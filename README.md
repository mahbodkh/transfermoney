
### **A Java RESTful API services for money transfers**

###### Run:

`$ mvn exec:java`

`url: http://localhost` 
`Port: 8888`




###### APIs:


| Http Method 	| Path                                   	| Description               	|
|-------------	|----------------------------------------	|---------------------------	|
| GET         	| /party/all                             	| GET all parties           	|
| GET         	| /party/{partyId}                       	| GET party with id         	|
| GET         	| /party/{username}                      	| GET by username           	|
| POST        	| /party/create                          	| Create party              	|
| PUT         	| /party/{partyId}                       	| EDIT party with id        	|
| DELETE      	| /party/{partyId}                       	| DELETE party with id      	|
| GET         	| /account/all                           	| GET all accounts          	|
| GET         	| /account/{accountId}                   	| GET account by id         	|
| GET         	| /account/{accountId}/balance           	| GET balance by account id 	|
| POST        	| /account/create                        	| Create account            	|
| PUT         	| /account/{accountId}/withdraw/{amount} 	| Withdraw money            	|
| PUT         	| /account/{accountId}/deposit/{amount}  	| Deposit money             	|
| DELETE      	| /account/{accountId}                   	| DELETE account by id      	|
| POST        	| /account/transaction                   	| Transfer money            	|
