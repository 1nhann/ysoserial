`src/main/resources/RCEECHO/Java-Rce-Echo` 是 github 上面直接 git cloen 下来的项目里面是一些rce 回显的 poc

`src/main/resources/RCEECHO/myRCEECHO` 是我修改过，可以用的

经过测试，作为 poc 的 jsp 代码里面，这些写法需要改一下：
1. 范式的写法，比如 `List<User>`，可以改成取出元素后再类型转换，把 Object 转成 User
2. for each 不能正常解析，要改成普通的 for 或者 while

