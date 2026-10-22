# java-open-rt-comm2（视频通信客户端 APP）源码历史归档

> 本仓库是 **只读历史档案（Archived）**，仅用于长期留存与查阅，不接受任何修改或推送。
> 据提供方说明，本工程为 **MIT 开源** 的「视频通信系统客户端 APP」；但本快照中**未包含 LICENSE 文件**（详见下文「许可证」）。

## 来源介质

| 项目 | 内容 |
| --- | --- |
| 介质文件 | `C:\Users\qycx1\Desktop\123\qycx2024\java-open-rt-comm2.zip.20241203.1.zip` |
| 快照日期 | 压缩包文件名标注 2024-12-03（提供方称为「2025 年 1 月」版本） |
| 压缩包大小 | 236,723,585 字节（约 225.7 MiB） |
| 压缩包 SHA-256 | `905cabe290b9979b71ad35ef8ae712b8e40401934b63b2457fea5f71f8437a11` |

> 原始压缩包体积超过 GitHub 单文件 100 MiB 上限，且内部约 260 MiB 为构建产物/缓存/调试 dump，
> 故**未将压缩包入库**。上表 SHA-256 即原始物证的完整性指纹，日后如需核验原始介质可据此比对。

## 技术栈

- 平台：Android（Gradle 构建，含 Gradle Wrapper）
- 语言：Java（主）+ JNI/NDK C/C++（原生库模块）
- 模块：
  - `app/` — 主应用模块（含 AndroidManifest、resources、Java 源码）
  - `tmpJavaLibrary/` — 原生库模块，含 `src/main/cpp/CMakeLists.txt` 与 C/C++ 源码
  - `doc/` — 文档（Markdown、drawio 图）
  - `gradle/` — Gradle Wrapper

## 归档处理说明

本档案由原始压缩包（9974 条目 / 解压约 301 MiB）解压整理而成，仅保留真实源码与资源。
剔除的内容如下（均无源码版本价值，或属不应公开之物）：

| 剔除类别 | 说明 | 约体积 |
| --- | --- | --- |
| `.git/` | 压缩包内嵌的完整 Git 仓库（含 `a_jiami2`/`dev`/`hgl`/`hgl_parse_h265` 等分支记录） | 93 MiB |
| `build/` | Gradle/Android 构建产物（`.dex`/`.class`/APK/中间文件） | 162 MiB |
| `.cxx/` `.idea/` `.gradle/` | NDK 构建缓存 / IDE / Gradle 缓存 | ~7 MiB |
| 预编译/依赖二进制 | `.so` `.jar` `.aar` `.apk` `.o` `.bin` `.flat` `.idx` `.pack` 等 | ~194 MiB |
| `111.zip` | 根目录文件，内部为 Android 设备 `SurfaceFlinger` proto 与 `FS/recovery` 文件系统 dump（调试产物） | 9.7 MiB |
| `dump_enc_toSend_*.mp3`、`test0X0.dump_procTaskData.mp3` | 根目录调试录音 dump | ~0.3 MiB |
| **`app/qycx.jks`** | **APK 签名密钥库（含私钥）—— 出于安全严禁入库，避免私钥泄露** | — |

**保留**：524 个文件 / 约 15 MiB，含 Java 源码、NDK C/C++ 源码、XML 布局与清单、Gradle 脚本、
图片资源（`res/raw/*.mp3` 等应用音频资源一并保留）、`doc/` 文档、Gradle Wrapper（`gradlew` 等）。

原始字节均由 `.gitattributes` 的 `* -text` 强制保留，不做换行符/编码规范化。

## 许可证

- 提供方声明本工程为 **MIT 开源**。
- **但本快照压缩包中未发现 LICENSE 文件**，故本归档仓库亦未附许可证文本。
- 所有源代码版权归原作者/原单位所有；如需基于本代码复用、修改或再分发，请自行取得权利人授权。
- 注意：原工程中的签名密钥库（`qycx.jks`）已被本归档剔除，请勿在任何公开位置提交签名私钥。
