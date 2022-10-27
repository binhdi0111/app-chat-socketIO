var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require('socket.io')(server);
var fs = require("fs");
server.listen(process.env.PORT || 3000);

// app.get("/", function(req, res){
// 	res.sendFile(__dirname + "/index.html");	
// });
console.log("Server running");

var arrayUser = [];
var tonTai = true;

io.sockets.on('connection',function(socket){
    console.log("co thiet bi vua ket noi");

    socket.on('client-register-user',function(data){
        if(arrayUser.indexOf(data) == -1){
            //không tồn tại user,đc phép đăng kí
            arrayUser.push(data);
            tonTai = false;
            console.log("đăng kí thành công user " + data);
            //gửi danh sách user về toàn bộ máy
            io.sockets.emit('server-send-user',{danhsach : arrayUser});
            //gán tên user cho socket
            socket.un = data;

        }else{
            console.log("đã tồn tại user " + data);
            tonTai = true;
        }

        //gửi kết quả đăng kí user từ server 
        socket.emit('server-send-result',{ketqua : tonTai});
    });
    socket.on('client-send-chat',function(noidung){
        console.log(socket.un + ": " + noidung);
        io.sockets.emit('server-send-chat',{chatcontent : socket.un + ": " + noidung });
    });
});