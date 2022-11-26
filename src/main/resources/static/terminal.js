const currentTerminalWindowSize = {};

const removePxAndParseInt = (str) => {
    return parseInt(str.replace("px", ""));
}
const initTerminalWindow = (term, socket) => {
    const terminalDom = document.getElementsByClassName("terminal");
    if (terminalDom && terminalDom.length !== 0) {
        terminalDom[0].style.height = (window.innerHeight * 0.95) + 'px';
        terminalDom[0].style.width = (window.innerWidth - 25) + 'px';
        // console.log(`resize terminal window: ${terminalDom[0].style.height}, ${terminalDom[0].style.width}`)
    }
    resizeTerm(term, socket);
}

const currentGeometry = () => {
    const terminalDom = document.getElementsByClassName("terminal");
    if (terminalDom && terminalDom.length !== 0) {
        const eleStyle = window.getComputedStyle(terminalDom[0]);
        const terminalWindowHeight = removePxAndParseInt(eleStyle.height);
        const terminalWindowWidth = removePxAndParseInt(eleStyle.width);

        if (!currentTerminalWindowSize.width || !currentTerminalWindowSize.height
            || currentTerminalWindowSize.height !== terminalWindowHeight || currentTerminalWindowSize.width !== terminalWindowWidth) {

            const width = '@'.pxWidth(`${eleStyle.fontSize} ${eleStyle.fontFamily}`);
            const height = width * 2;
            const columns = parseInt( terminalWindowWidth / width, 10);
            const rows = parseInt(terminalWindowHeight / height, 10);
            currentTerminalWindowSize.width = terminalWindowWidth;
            currentTerminalWindowSize.height = terminalWindowHeight;
            return { columns, rows };
        }
    }
};
const resizeTerm = (term, ws) => {
    const size = currentGeometry();
    if (size) {
        const columns = size.columns;
        const rows = size.rows;
        // console.log(`resizing term to ${JSON.stringify({ columns, rows })}`);
        term.resize(columns, rows);
        ws.send(JSON.stringify({type: 'RESIZE', data: `columns=${columns};rows=${rows}`}));
    }
};

String.prototype.pxWidth = function(font) {
    // re-use canvas object for better performance
    var canvas = String.prototype.pxWidth.canvas || (String.prototype.pxWidth.canvas = document.createElement("canvas")),
        context = canvas.getContext("2d");

    font && (context.font = font);
    var metrics = context.measureText(this);

    return metrics.width;
}
window.addEventListener('load', function () {
    const socket = new WebSocket('ws://localhost:8080/ws');
    socket.onopen = function () {
        socket.send(JSON.stringify({type: 'OPEN', data: ''}));
        const term = new Terminal({cols: 20, rows: 10, screenKeys: true});
        socket.onmessage = function (event) {
            if (event.type === 'message') {
                const data = JSON.parse(event.data)['data'];
                term.write(data);
            }
        };
        socket.onclose = function () {
            socket.send(JSON.stringify({type: 'CLOSE', data: ''}));
            socket.onmessage = null;
            socket.onclose = null;
            term.destroy();
        };
        term.on('data', function (data) {
            socket.send(JSON.stringify({type: 'TERMINAL_INPUT', data: data}));
        });
        term.open(document.body);
        initTerminalWindow(term, socket);

        window.onresize = (e) => {
            initTerminalWindow(term, socket);
        }
    };
});