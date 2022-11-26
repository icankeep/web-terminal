## Web Terminal

A Web terminal based on SpringBoot Websocket

## TODO List
- [x] optimize code
- [x] resize window
- [x] support window system
- [ ] terminal management

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
docker run -p 8080:8080 icankeep/web-terminal:latest

# open browser http://localhost:8080/
```

