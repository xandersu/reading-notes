# 第6章 banner解析 

## 默认banner

无操作

## 文字banner

- 设置banner.txt

- 设置spring.banner.location

## 图片banner

- 设置banner.(gif/png/jpg)
- 设置spring.banner.image.location

## 兜底banner

- SpringApplication.setBanner()

## 关闭banner

- Spring.main.banner-mode=off 

## 输出banner逻辑

- 获取banner
- 打印banner

## getImageBanner

- 可以通过spring.banner.image.location指定位置
- 可使用的格式gif、png、jpg

## getTextBanner

- 可以通过spring.banner.location指定位置
- 默认banner.txt

## 获取banner步骤

Start => 添加banner.png/banner.jpg/banner.gif至banners => 添加banner.txt至banners => banner是否为空 （否 => 返回banners） => fallbackBanner是否为空（否，返回fallbackBanner）=> 是，返回DEAFAULT_BANNER



# banner输出原理

## 默认输出

1. 输出banner指定内容
2. 获取version信息
3. 文本内容前后对齐
4. 文本内容染色
5. 输出文本内容

## 文本输出

- 可以通过spring.banner.charset指定字符集
- 获取文本内容
- 替换占位符
- 输出文本内容

## 图案输出

- 可以通过spring.banner.image.*设置图片属性
- 读取文件流
- 输出图片内容

