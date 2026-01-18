# Deploying DemoBank to Render

This guide will help you deploy the DemoBank Spring Boot application to Render.

## Prerequisites

1. A [Render account](https://render.com) (free tier available)
2. A MongoDB database (you can use MongoDB Atlas free tier)
3. This repository pushed to GitHub

## Deployment Steps

### Step 1: Prepare Your MongoDB Database

If you're using MongoDB Atlas:
1. Go to [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a free cluster if you haven't already
3. Create a database user with read/write permissions
4. Get your connection string (should look like: `mongodb+srv://username:password@cluster.mongodb.net/database-name`)
5. Whitelist all IP addresses (0.0.0.0/0) for Render to connect

### Step 2: Deploy to Render

#### Option A: Using render.yaml (Recommended - Infrastructure as Code)

1. **Push this repository to GitHub** (if not already done)

2. **Go to [Render Dashboard](https://dashboard.render.com/)**

3. **Click "New +" → "Blueprint"**

4. **Connect your GitHub repository**
   - Authorize Render to access your GitHub account
   - Select the `demobank` repository

5. **Configure the Blueprint**
   - Render will automatically detect the `render.yaml` file
   - Click "Apply" to create the service

6. **Set Environment Variables**
   - After creating the service, go to the service settings
   - Add the following environment variable:
     - **Key**: `MONGODB_URI`
     - **Value**: Your MongoDB connection string
       ```
       mongodb+srv://username:password@cluster.mongodb.net/database-name
       ```

7. **Deploy**
   - Click "Manual Deploy" → "Deploy latest commit"
   - Wait for the build to complete (may take 5-10 minutes)

#### Option B: Manual Setup

1. **Go to [Render Dashboard](https://dashboard.render.com/)**

2. **Click "New +" → "Web Service"**

3. **Connect your repository**
   - Select your GitHub repository
   - Choose the branch to deploy (usually `main` or `master`)

4. **Configure the service**
   - **Name**: `demobank` (or your preferred name)
   - **Runtime**: `Docker`
   - **Build Command**: `./build.sh`
   - **Start Command**: `./start.sh`
   - **Plan**: `Free` (or choose a paid plan for production)

5. **Add Environment Variables**
   - Click "Advanced" → "Add Environment Variable"
   - Add the following:
     - **MONGODB_URI**: Your MongoDB connection string
     - **JAVA_VERSION**: `17`
     - **MAVEN_VERSION**: `3.9.12`

6. **Create Web Service**
   - Click "Create Web Service"
   - Wait for the deployment to complete

### Step 3: Access Your Application

Once deployed, Render will provide you with a URL like:
```
https://demobank.onrender.com
```

Your application will be accessible at this URL!

## Important Configuration

### Environment Variables

Make sure to set these environment variables in Render:

| Variable | Description | Example |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb+srv://user:pass@cluster.mongodb.net/dbname` |
| `JAVA_VERSION` | Java version (optional) | `17` |
| `MAVEN_VERSION` | Maven version (optional) | `3.9.12` |

### Database Connection

The application is configured to use the `MONGODB_URI` environment variable. Make sure:
- Your MongoDB cluster allows connections from all IPs (0.0.0.0/0)
- Your connection string includes the database name
- Your database user has read/write permissions

## Troubleshooting

### Build Failures

If the build fails:
1. Check the build logs in Render dashboard
2. Ensure Java 17 is being used
3. Verify Maven can download dependencies

### Connection Issues

If the application can't connect to MongoDB:
1. Verify the `MONGODB_URI` environment variable is set correctly
2. Check MongoDB Atlas network access settings (whitelist 0.0.0.0/0)
3. Ensure database user credentials are correct
4. Check the MongoDB cluster is running

### Application Crashes

If the application starts but crashes:
1. Check the application logs in Render dashboard
2. Look for MongoDB connection errors
3. Verify environment variables are set
4. Check if the port configuration is correct

## Seed Database (Optional)

If you want to seed your database with initial data:

1. **Connect to your MongoDB database** using MongoDB Compass or the mongo shell

2. **Run the seed scripts** from the `database-seed` directory in this repository

3. **Restart your Render service** to ensure it picks up the new data

## Free Tier Limitations

On Render's free tier:
- Your service will spin down after 15 minutes of inactivity
- The first request after spin-down may take 30-60 seconds to respond
- You get 750 hours of runtime per month (sufficient for one service)

For production use, consider upgrading to a paid plan.

## Updating Your Application

To deploy updates:

1. **Push changes to GitHub**
   ```bash
   git add .
   git commit -m "Update application"
   git push
   ```

2. **Render will automatically redeploy** if you have auto-deploy enabled

3. Or **manually trigger a deploy** from the Render dashboard

## Security Recommendations

⚠️ **Before deploying to production:**

1. **Change default credentials**
   - Update the default admin credentials in `application.properties`
   - Use strong passwords

2. **Use environment variables for secrets**
   - Never commit sensitive data to GitHub
   - Use Render's environment variables for all secrets

3. **Enable HTTPS**
   - Render provides free SSL certificates automatically

4. **Configure security headers**
   - Consider adding security headers in your Spring Security configuration

5. **Set up monitoring**
   - Use Render's built-in logging
   - Consider integrating with external monitoring tools

## Additional Resources

- [Render Documentation](https://render.com/docs)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## Support

If you encounter issues:
1. Check the application logs in Render dashboard
2. Review the MongoDB Atlas metrics
3. Refer to this guide and the official documentation
4. Open an issue in the GitHub repository

---

**Happy Deploying! 🚀**
