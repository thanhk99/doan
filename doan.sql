DROP DATABASE IF EXISTS doan;
create schema doan;
use doan;
create table users(
	id int primary key auto_increment ,
    tk char(40),
    mk char(40),
    fullname text,
    email text
);
insert into users(tk,mk,fullname,email) values 
('thanhdz','1','Phạm Như Thành','thanhit482004@gmail.com'),
('thanhcute','1','Nguyễn Văn Thiệu','thieucho@gmail.com'),
('thanh','1','Hạ Hầu Đôn','downmonkey@gmail.com'),
('thanh2004','1','Nguyễn Đình Hưng','thanhcute@gmail.com'),
('admin','admin','Admin','');


create table listgame(
	id int primary key auto_increment ,
    namegame char(40) 
);
insert into listgame(namegame) values 
('Đoán số '),
('Bầu cua'),
('Vòng quay may mắn'),
('Slot 777');

create table sessionplayer(
	id int primary key auto_increment,
    namegame char(40) ,
    playerid int ,
    timeoccurs char(50),
    result char(20),
    bet float ,
    reward float,
    choice char(20),
    foreign key (playerid) references users(id)
);
#insert into sessionplayer(gameid,playerid ,timeoccurs,result ,pointbet,pointrc,bet)values();

create table sessiongame (
	 id int primary key auto_increment,
--      idgame int ,
     namegame char(40),
     result char(10),
     timeoccurs char(50) 
--      foreign key (idgame) references listgame(id)
);
#insert into sessiongame(idgame,namegame,result,timeoccurs)values(1,'Đoán số ','L','2024-10-25 01:06:45');

create table atm(
	idplayer int ,
    stk char(40) unique,
    balance float ,
    foreign key (idplayer) references users(id)
);
insert into atm values
(1,0787107821,5000),
(2,04082004,5000),
(3,30041975,5000),
(4,66668888363636,5000);

create table historybalance(
	id int primary key auto_increment ,
    idplayer int ,
    timechange datetime,
    content text,
    trans float ,
    balance float ,
    foreign key (idplayer) references users(id)
);
#insert into historybalance value()

-- create table mission(
--  id int primary key auto_increment,
--  request float ,
--  award float ,
--  statu text 
-- );
-- insert into mission(request,award,statu) values
-- (1000,100),
-- (5000,500),
-- (20000,1000),
-- (50000,2000),
-- (100000,5000),
-- (200000,10000);

create table recive(
	id int primary key auto_increment,
    idplayer int ,
    request float ,
    daterecive date
);

create table store(
	percent int primary key ,
    price int ,
    img text
);
insert into store value 
(25,25000,'/img'),
(50,75000,'/img'),
(75,150000,'/img'),
(90,250000,'/img'),
(100,1000000,'/img');

create table historyBuy (
	id int primary key auto_increment,
    idstore int ,
    datebuy date
);

create table friend(
	id int primary key auto_increment,
    id_my int ,
    id_friend int ,
    relative char(50),
    foreign key (id_my) references users(id),
    foreign key (id_friend) references users(id)
);