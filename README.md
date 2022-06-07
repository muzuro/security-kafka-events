### start kafka docker

```bash
docker-compose up -d
```

### ensure kafka working

```bash
docker ps
```

### check kafka logs

```bash
docker log security-kafka-events_kafka_1
```

### run postman collection(login and update email endpoints)
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/8bb1557f1d1e3361d7a5?action=collection%2Fimport)