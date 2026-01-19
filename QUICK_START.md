# Quick Start - Render Deployment

## Prerequisites
- MongoDB Atlas account with connection string
- Render account (free tier available)
- Repository connected to Render

## Deployment Steps

### 1. MongoDB Setup (5 minutes)
```
1. Go to mongodb.com/cloud/atlas
2. Create free cluster
3. Create database user
4. Configure network access (0.0.0.0/0 for Render)
5. Copy connection string
```

### 2. Render Setup (2 minutes)
```
1. Go to dashboard.render.com
2. Click "New +" → "Blueprint"
3. Connect your GitHub repository
4. Render detects render.yaml automatically
```

### 3. Configure Environment (1 minute)
```
In Render dashboard:
- Go to your service → Environment
- Add: MONGO_URI = <your-mongodb-connection-string>
- Save
```

### 4. Deploy! (5-10 minutes)
```
Render automatically:
- Builds Docker image
- Runs tests
- Deploys application
- Provides public URL
```

## Your Application URLs
- **Main App**: `https://e-wallet-biz.onrender.com`
- **Health Check**: `https://e-wallet-biz.onrender.com/actuator/health`

## Troubleshooting

**Build fails?**
- Check build logs in Render dashboard
- Verify Java 17 compatibility

**App won't start?**
- Verify MONGO_URI is set correctly
- Check MongoDB allows connections from 0.0.0.0/0
- Review application logs in Render

**Health check fails?**
- Wait 2-3 minutes for app to fully start
- Check `/actuator/health` endpoint manually
- Verify port configuration (Render auto-sets PORT)

## Free Tier Notes
- App spins down after 15 minutes of inactivity
- First request after spin-down takes 30-60 seconds
- No cost for this tier

## Need Help?
See DEPLOYMENT.md for detailed instructions and troubleshooting.
