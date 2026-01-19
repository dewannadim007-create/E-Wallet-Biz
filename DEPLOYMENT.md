# Deployment Guide for Render

This guide will help you deploy the E-Wallet-Biz application to Render using Docker and MongoDB.

## Prerequisites

1. A [Render account](https://render.com) (free tier available)
2. A MongoDB database (you can use [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) free tier)
3. Git repository connected to Render

## Step 1: Set Up MongoDB

If you don't have a MongoDB instance:

1. Go to [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a free cluster
3. Create a database user with password
4. Configure network access:
   - For Render deployments, you may need to allow access from all IPs (0.0.0.0/0) as Render uses dynamic IPs
   - **Security Note**: This allows connections from any IP. For production, consider using MongoDB Atlas's VPC peering or PrivateLink for enhanced security
   - Alternatively, regularly update the whitelist with Render's IP ranges (contact Render support for current ranges)
5. Get your connection string (it will look like: `mongodb+srv://<username>:<password>@cluster.mongodb.net/<database>`)

## Step 2: Deploy to Render

### Option A: Using render.yaml (Automatic)

1. **Connect Your Repository**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New +" → "Blueprint"
   - Connect your GitHub/GitLab repository
   - Render will automatically detect the `render.yaml` file

2. **Configure Environment Variables**
   - After the blueprint is created, go to your web service settings
   - Add the environment variable:
     - `MONGO_URI`: Your MongoDB connection string
   - Example: `mongodb+srv://username:password@cluster.mongodb.net/mobile-banking`

3. **Deploy**
   - Render will automatically build and deploy your application
   - The build process uses the Dockerfile

### Option B: Manual Deployment

1. **Create a Web Service**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New +" → "Web Service"
   - Connect your repository
   - Configure:
     - **Name**: e-wallet-biz (or your preferred name)
     - **Runtime**: Docker
     - **Branch**: main (or your default branch)
     - **Plan**: Free (or upgrade as needed)

2. **Environment Variables**
   Add these in the "Environment" section:
   - `MONGO_URI`: Your MongoDB connection string
   - `JAVA_OPTS`: `-Xmx512m -Xms256m` (optional, for memory tuning)
   - `SPRING_PROFILES_ACTIVE`: `prod` (optional)

3. **Health Check**
   - Path: `/actuator/health`
   - Render will use this to verify your app is running

4. **Deploy**
   - Click "Create Web Service"
   - Render will build the Docker image and deploy

## Step 3: Verify Deployment

1. Wait for the build to complete (first build may take 5-10 minutes)
2. Once deployed, Render will provide a URL (e.g., `https://e-wallet-biz.onrender.com`)
3. Visit the URL to see your application
4. Check the health endpoint: `https://your-app.onrender.com/actuator/health`

## Environment Variables Reference

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `MONGO_URI` | Yes | MongoDB connection string | `mongodb+srv://user:pass@cluster.mongodb.net/dbname` |
| `PORT` | No | Application port (auto-set by Render) | `8080` |
| `JAVA_OPTS` | No | JVM options for tuning | `-Xmx512m -Xms256m` |
| `SPRING_PROFILES_ACTIVE` | No | Spring profile to activate | `prod` |

## Important Notes

### Security
- **Never commit your MongoDB credentials** to the repository
- Always use environment variables for sensitive data
- The current `application.properties` has a fallback connection string for local development only
- In production, always set `MONGO_URI` environment variable

### MongoDB Connection String Format
```
mongodb+srv://<username>:<password>@<cluster-hostname>.mongodb.net/<database>?retryWrites=true&w=majority
```

### Free Tier Limitations
- Render free tier spins down after 15 minutes of inactivity
- First request after spin-down may take 30-60 seconds
- For production, consider upgrading to a paid plan

### Troubleshooting

**Build fails:**
- Check the build logs in Render dashboard
- Ensure Java 17 is compatible with your code
- Verify `pom.xml` has no errors

**Application doesn't start:**
- Check the logs in Render dashboard
- Verify `MONGO_URI` is set correctly
- Ensure MongoDB cluster allows connections from all IPs (0.0.0.0/0)

**Health check fails:**
- Verify the application is running on the port Render assigns
- Check `/actuator/health` endpoint is accessible
- Review application logs for errors

## Local Testing with Docker

Before deploying, test locally:

```bash
# Build the Docker image
docker build -t e-wallet-biz .

# Run with environment variables
docker run -p 8080:8080 \
  -e MONGO_URI="your_mongodb_connection_string" \
  e-wallet-biz

# Test the application
curl http://localhost:8080/actuator/health
```

## Continuous Deployment

Render automatically redeploys when you push to your configured branch:

1. Make changes to your code
2. Commit and push to GitHub/GitLab
3. Render will automatically build and deploy the new version

## Support

For issues specific to:
- **Render**: Check [Render Documentation](https://render.com/docs)
- **MongoDB**: Check [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- **Spring Boot**: Check [Spring Boot Documentation](https://spring.io/projects/spring-boot)
