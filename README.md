# hibernate-envers-HHH-12186
Reproducer for Hibernate Envers bug [HHH-12186](https://hibernate.atlassian.net/browse/HHH-12186)

If you use docker, get a local Oracle XE up with the following command

```
$ docker run -d --shm-size=2g -p 1521:1521 -p 8080:8080 alexeiled/docker-oracle-xe-11g
```

Then ensure that the port is accessible for the Java Runtime where you run the tests.
