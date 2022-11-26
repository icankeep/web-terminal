## Web Terminal

A Web terminal based on SpringBoot Websocket

## TODO List
- [ ] optimize code
- [ ] resize window
- [ ] support window

## Run

### Running from source
JDK 11 or later

```bash
git clone git@github.com:icankeep/web-terminal.git
cd web-terminal
mvn clean spring-boot:run -Dserver.port=8080

# open browser http://localhost:8080/
```
![browser.png](./docs/images/img.png)

### Running from docker
```bash
docker run -p 8080:8080 icankeep/web-terminal:0.0.1.beta1.6832523

# open browser http://localhost:8080/
```

