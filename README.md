# 🛍️ Bag Store — Android E-Commerce Application

A modern Android e-commerce application built with **Kotlin and Jetpack Compose**, integrated with the Bag Store backend powered by Django REST Framework.

This project demonstrates Android application development, REST API integration, authentication, token management, dependency injection, and communication between a mobile application and a backend server.

## ✨ Features

- 🔐 User Authentication
- 👤 User Registration and Login
- 🛍️ Product Data Integration
- 🌐 REST API Communication
- 🔄 Token Management
- 🔑 Refresh Token Support
- 🧭 Navigation Graph
- 🧩 Dependency Injection
- 📡 Network Connectivity Utilities
- 📱 Native Android UI with Jetpack Compose

*Features such as product browsing, token refresh, and other operations depend on the corresponding implementations being active in the application.*

## 🛠️ Tech Stack

### Android Application

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | UI development |
| MVVM-oriented structure | Separation of UI and data responsibilities |
| Retrofit | HTTP API communication |
| Hilt / Dependency Injection | Dependency management, where configured |
| TokenInterceptor | Token-related HTTP request handling |
| Kotlin Coroutines | Asynchronous operations, where used |

### Backend

The application connects to the Bag Store backend, which uses:

- Python
- Django
- Django REST Framework
- JWT Authentication

**Backend Repository:** [amir-khaksar/bag-store](https://github.com/amir-khaksar/bag-store)

## 🏗️ Architecture

The application uses a client-server architecture. The Android client communicates with the Django REST API to exchange data and perform supported operations.

```text
┌─────────────────────────────────┐
│       Android Application       │
│                                 │
│       Jetpack Compose UI        │
│               ↓                 │
│         Navigation Graph        │
│               ↓                 │
│          Repository             │
│               ↓                 │
│            Retrofit             │
│               ↓                 │
│         TokenInterceptor        │
└───────────────┬─────────────────┘
                │
                │ HTTP / JSON
                ▼
┌─────────────────────────────────┐
│        Django REST API          │
│                                 │
│       Authentication            │
│       Product Endpoints         │
│       Cart and Order APIs       │
│               ↓                 │
│            Database             │
└─────────────────────────────────┘
```

The diagram illustrates the general communication flow. The exact request flow depends on the implementation of individual screens and repository methods.

## 📂 Project Structure

The Android application separates application initialization, data sources, dependency injection, models, repositories, network services, navigation, and UI components.

```text
app/src/main/java/com/example/loginproject/
│
├── base/
│   └── MyApp.kt
│
├── data/
│   ├── local/
│   ├── remote/
│   └── service/
│
├── di/
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── models/
│   ├── LoginModel.kt
│   ├── LoginResponseModel.kt
│   ├── LoginTokenResponseModel.kt
│   ├── RefreshRequest.kt
│   ├── RefreshResponseModel.kt
│   ├── RegisterModel.kt
│   ├── RegisterResponseModel.kt
│   └── User.kt
│
├── navigation/
│   └── NavGraph.kt
│
├── repository/
│   └── ApiRepository.kt
│
├── services/
│   ├── RetrofitInstance.kt
│   └── TokenInterceptor.kt
│
├── ui/
│
├── utilities/
│   └── NetworkUtils.kt
│
└── MainActivity.kt
```

### 📁 Package Responsibilities

| Package / File | Description |
|---|---|
| `base/` | Application-level initialization |
| `data/local/` | Local data access |
| `data/remote/` | Remote data access |
| `data/service/` | Data-related service components |
| `di/` | Network and repository dependency configuration |
| `models/` | Authentication, registration, token, and user data models |
| `navigation/` | Navigation graph and screen navigation |
| `repository/` | Repository layer for API-related operations |
| `services/` | Retrofit setup and token interception |
| `ui/` | UI screens and components |
| `utilities/` | Network utility functions |
| `MainActivity.kt` | Main Android activity |

## 🚀 Getting Started

Follow the instructions below to configure the backend and run the Android application locally.

### Prerequisites

Make sure you have the following installed:

- Android Studio
- Android SDK compatible with the project
- A compatible JDK
- Python 3
- Git
- An Android emulator or physical Android device

## ⚙️ 1. Set Up the Backend

The Android application uses the Bag Store Django REST API.

### Clone the Repository

Open a terminal and run:

```bash
git clone https://github.com/amir-khaksar/bag-store.git
cd bag-store/backend
```

### Create a Virtual Environment

**Windows**

```bash
python -m venv venv
venv\Scripts\activate
```

**Linux / macOS**

```bash
python3 -m venv venv
source venv/bin/activate
```

### Install Dependencies

Install the Python dependencies listed in `req.txt`:

```bash
pip install -r req.txt
```

### Apply Database Migrations

```bash
python manage.py migrate
```

### Start the Development Server

Run Django with the following command:

```bash
python manage.py runserver 0.0.0.0:8000
```

The backend will listen on port `8000` on the computer's available network interfaces.

Keep the terminal open while using the Android application.

The backend repository may require additional configuration depending on its settings, environment variables, and database setup.

## 📱 2. Configure the Android Application

Open the Android project in Android Studio and allow Gradle to synchronize the project dependencies.

Before running the application, configure the API base URL to point to the running backend.

### 🔌 Connecting a Physical Android Device

When Django runs on your computer, using `localhost` or `127.0.0.1` in the Android application will not connect to that computer from a separate physical phone.

Instead, use the computer's **local IPv4 address**.

#### Step 1: Find Your Computer's IP Address

On Windows, open Command Prompt and execute:

```bash
ipconfig
```

Find the IPv4 address of the network adapter connected to the same local network as your Android device.

Example:

```text
192.168.1.100
```

Your actual IP address will depend on your network.

#### Step 2: Configure the API Base URL

Update the base URL in the Android application's network configuration.

Example:

```kotlin
const val BASE_URL = "http://192.168.1.100:8000/"
```

Replace `192.168.1.100` with the actual IPv4 address of your computer.

The base URL must match the backend address and port. API endpoint paths should be configured according to the backend's actual routes.

#### Step 3: Ensure Network Connectivity

For a physical Android device:

1. Connect the phone and computer to the same Wi-Fi network.
2. Keep the Django development server running.
3. Ensure Windows Firewall permits incoming connections to port `8000` on the relevant private network.
4. Verify that your router allows communication between connected devices.
5. Confirm that Django accepts requests for the configured host.

If Django returns a `DisallowedHost` error, review the `ALLOWED_HOSTS` setting in the backend configuration.

### 🤖 Connecting an Android Emulator

When using the standard Android Studio emulator, you can usually access the development computer through:

```text
http://10.0.2.2:8000/
```

This special address maps to the host computer's loopback interface from the emulator.

### 🌐 Base URL Reference

| Environment | Example URL |
|---|---|
| Android Studio Emulator | `http://10.0.2.2:8000/` |
| Physical Android Device | `http://192.168.1.100:8000/` |
| Browser on the Backend Computer | `http://127.0.0.1:8000/` |

The physical-device URL is an example. Use your computer's actual IP address.

### ⚠️ HTTP and Android Network Security

Local development may use HTTP instead of HTTPS. Depending on the Android application's network security configuration and target SDK, unencrypted HTTP traffic may be blocked.

If necessary, configure cleartext traffic for development only. Production deployments should use HTTPS.

## 🔑 Authentication and Token Handling

The project contains dedicated models and networking components for authentication and token-related operations.

Relevant components include:

- `LoginModel.kt` — login request data.
- `LoginResponseModel.kt` — login response data.
- `LoginTokenResponseModel.kt` — token-related login response.
- `RegisterModel.kt` — registration request data.
- `RegisterResponseModel.kt` — registration response data.
- `RefreshRequest.kt` — refresh-token request data.
- `RefreshResponseModel.kt` — refresh-token response data.
- `TokenInterceptor.kt` — token-related HTTP request interception.

The actual authentication mechanism, token storage strategy, and refresh behavior depend on the corresponding implementations.

## 🧰 Troubleshooting

### The Application Cannot Connect to the Backend

Check the following:

- The Django server is running.
- The backend uses `0.0.0.0:8000` when accessed from another device.
- The configured IP address is correct.
- The phone and computer can communicate over the local network.
- Windows Firewall is not blocking the connection.
- Django's `ALLOWED_HOSTS` configuration permits the requested host.
- Android's network security configuration permits the intended development traffic.

### Connection Refused

Verify that the backend is running and listening on the expected port. Also check firewall rules and network connectivity.

### DisallowedHost Error

Django may reject a request if its host is not included in `ALLOWED_HOSTS`. Configure the appropriate development host instead of disabling host validation indiscriminately.

### The Computer's IP Address Changes

Local IP addresses may change when reconnecting to a network. If this happens, update the API base URL in the Android application.

### API Requests Fail Despite a Successful Connection

Verify the endpoint path, HTTP method, request body, authentication headers, and response format against the backend API.

## 🔒 Security Considerations

- Use HTTPS for production deployments.
- Never commit production secrets or private credentials.
- Validate and authorize requests on the backend.
- Configure development and production environments separately.
- Avoid exposing the Django development server to the public internet.
- Protect authentication tokens and sensitive user information.
- Use appropriate host and network access restrictions.

## 👨‍💻 Contributors

| Role | Contributor |
|---|---|
| Android Developer | [Add your GitHub username](https://github.com/) |
| Frontend Developer | [@amir-khaksar](https://github.com/amir-khaksar) |
| Backend Developer | [@mahan-rezaei](https://github.com/mahan-rezaei) |

Update the contributor list according to the actual responsibilities and repository ownership.

## 🛣️ Future Improvements

Potential improvements include:

- Automated unit and integration tests.
- Improved error handling and retry strategies.
- Additional product browsing and filtering capabilities.
- Enhanced offline support.
- Improved token lifecycle management.
- Production deployment and monitoring.

These are possible future improvements rather than claims about existing functionality.

## 📄 License

No license has been specified in this README. Add a license section when the project's distribution terms have been decided.

## ⭐ Support

If you find the project useful, consider giving the [Bag Store backend repository](https://github.com/amir-khaksar/bag-store) a star.

---

**Built with Kotlin, Jetpack Compose, Retrofit, and Django REST Framework.**
