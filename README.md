# Blockchain.com Challenge
API to retrieve data from exchanges


### Allowed Exchanges :

- blockchaindotcomechallenge

### RUN locally :

#### Prerequisites:

- Docker

```
docker build . -t blockchainchallenge
```

```
docker run blockchainchallenge
```

### Example :
https://blockchaindotcom-challenge.herokuapp.comexchanges/blockchaindotcom/order-books?sorted=true

- For further details check [openapi.yaml](https://github.com/mmiguenz/BlockchainDotComChallenge/blob/main/openapi.yml)