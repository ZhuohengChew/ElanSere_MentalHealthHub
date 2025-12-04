spring:
  application:
    name: mental-health-hub
    
  mvc:
    view:
      prefix: /templates/
      suffix: .html

logging:
  level:
    root: INFO
    com.mentalhealthhub: DEBUG

server.port=8080
server.servlet.context-path=/mentalhealthhubs