# TariqMap Roadmap Generator

TariqMap is a Spring Boot application that leverages OpenAI's API to generate personalized learning roadmaps for various tech disciplines and career paths.

## 🚀 Overview

TariqMap helps users navigate their learning journey by creating customized roadmaps based on their specific goals, current skill level, and learning preferences. The application uses AI to generate step-by-step guidance, recommended resources, and a structured path to follow.

## ✨ Features

- **Personalized Roadmaps**: Generate tailored learning paths based on your inputs
- **Multiple Disciplines**: Support for various tech fields including programming, data science, cybersecurity, and more
- **Email Delivery**: Receive your roadmap directly to your inbox
- **User-Friendly Interface**: Simple and intuitive UI for easy interaction
- **Responsive Design**: Access from any device - desktop, tablet, or mobile

## 🛠️ Technologies

- **Backend**: Java with Spring Boot
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **AI Integration**: OpenAI API
- **Email Service**: Spring Mail with SMTP

## 🔧 Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven
- OpenAI API key
- Email account for sending roadmaps

### Environment Configuration
1. Clone the repository:
   ```
   git clone https://github.com/eslamyounis1/Tariq_Roadmap_Generator.git
   cd Tariq_Roadmap_Generator
   ```

2. Create a `.env` file in the project root with the following:
   ```
   OPENAI_API_KEY=your_openai_api_key
   MAIL_USERNAME=your_email_address
   MAIL_PASSWORD=your_email_app_password
   ```

3. Configure `application.properties` to use these environment variables:
   ```
   spring.application.name=tariqmap
   openai.api.key=${OPENAI_API_KEY}
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=${MAIL_USERNAME}
   spring.mail.password=${MAIL_PASSWORD}
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

### Building and Running
1. Build the application:
   ```
   mvn clean package
   ```

2. Run the application:
   ```
   java -jar target/tariqmap-0.0.1-SNAPSHOT.jar
   ```

3. Access the application at `http://localhost:8080`

## 📱 Usage

1. Visit the main page and select your desired tech field
2. Search for specific skills you want to learn
3. Submit the form to generate your personalized roadmap
4. Receive the roadmap via email and view it on the application

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 👨‍💻 Author

- Eslam Younis - [GitHub Profile](https://github.com/eslamyounis1)

## 🙏 Acknowledgements

- OpenAI for providing the API that powers the roadmap generation
- Spring Boot for the robust application framework
- All contributors and supporters of this project