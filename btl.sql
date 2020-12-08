-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 08, 2020 lúc 05:12 PM
-- Phiên bản máy phục vụ: 10.4.11-MariaDB
-- Phiên bản PHP: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `btl`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `resultgame`
--

CREATE TABLE `resultgame` (
  `idResult` int(11) NOT NULL,
  `winner` varchar(45) NOT NULL,
  `loser` varchar(45) NOT NULL,
  `winner_fi_time` varchar(45) NOT NULL,
  `loser_fi_time` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `resultgame`
--

INSERT INTO `resultgame` (`idResult`, `winner`, `loser`, `winner_fi_time`, `loser_fi_time`) VALUES
(1, 'tien', 'tung', '00:00:01:21', '00:00:02:67'),
(2, 'tien', 'tung', '00:00:00:94', '00:00:03:03'),
(3, 'tien', 'tung', '00:00:04:93', '00:00:06:26'),
(4, 'tien', 'tung', '00:00:02:34', '00:00:03:76');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `hoten` varchar(45) COLLATE latin1_bin NOT NULL,
  `username` varchar(45) COLLATE latin1_bin NOT NULL,
  `pass` varchar(45) COLLATE latin1_bin NOT NULL,
  `points` double NOT NULL,
  `isonl` tinyint(1) NOT NULL DEFAULT 0,
  `status` int(11) NOT NULL,
  `games` int(11) NOT NULL,
  `totaltime` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `hoten`, `username`, `pass`, `points`, `isonl`, `status`, `games`, `totaltime`) VALUES
(1, 'tien', 'tien', '123', 71, 1, 0, 0, 0),
(2, 'tung', 'tung', '123', 12.5, 0, 0, 0, 0),
(3, 'tiennnn', 'tiennnn', '123', 123, 0, 0, 0, 0);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `resultgame`
--
ALTER TABLE `resultgame`
  ADD PRIMARY KEY (`idResult`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idnew_table_UNIQUE` (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `resultgame`
--
ALTER TABLE `resultgame`
  MODIFY `idResult` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
