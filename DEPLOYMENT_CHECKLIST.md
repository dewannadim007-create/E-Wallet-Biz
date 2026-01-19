# Deployment Checklist

Use this checklist to ensure you complete all steps for deploying to Render.

## Pre-Deployment

### MongoDB Atlas Setup
- [ ] Create MongoDB Atlas account
- [ ] Create a free cluster
- [ ] Create database user with strong password
- [ ] Configure network access (0.0.0.0/0 for Render)
- [ ] Get MongoDB connection string
- [ ] Test connection string locally (optional)

### Render Account Setup
- [ ] Create Render account (https://render.com)
- [ ] Connect GitHub account to Render
- [ ] Verify repository access

## Deployment

### Initial Setup
- [ ] Go to Render Dashboard
- [ ] Click "New +" → "Blueprint"
- [ ] Select your repository: E-Wallet-Biz
- [ ] Render detects `render.yaml` automatically
- [ ] Confirm blueprint creation

### Environment Configuration
- [ ] Navigate to your web service settings
- [ ] Click "Environment" tab
- [ ] Add environment variable:
  - Key: `MONGO_URI`
  - Value: `mongodb+srv://username:password@cluster.mongodb.net/mobile-banking`
- [ ] Click "Save Changes"

### Deploy
- [ ] Render automatically starts building
- [ ] Wait for build to complete (5-10 minutes)
- [ ] Check build logs for any errors
- [ ] Wait for deployment to complete

## Post-Deployment Verification

### Health Checks
- [ ] Visit your app URL: `https://e-wallet-biz.onrender.com`
- [ ] Check health endpoint: `https://e-wallet-biz.onrender.com/actuator/health`
- [ ] Verify health check returns `{"status":"UP"}`

### Application Testing
- [ ] Test login functionality
- [ ] Verify database connection
- [ ] Test key features:
  - [ ] User authentication
  - [ ] Transaction operations
  - [ ] Balance inquiry
  - [ ] Money transfer

### Monitoring
- [ ] Check Render logs for any errors
- [ ] Monitor application performance
- [ ] Set up alerts (optional)

## Troubleshooting

If you encounter issues:

### Build Fails
- [ ] Check Render build logs
- [ ] Verify Dockerfile syntax
- [ ] Ensure pom.xml has no errors
- [ ] Check Java 17 compatibility

### Application Won't Start
- [ ] Verify MONGO_URI is set correctly
- [ ] Check MongoDB network access settings
- [ ] Review Render application logs
- [ ] Verify port configuration

### Database Connection Issues
- [ ] Test MongoDB connection string locally
- [ ] Verify MongoDB user permissions
- [ ] Check MongoDB Atlas IP whitelist
- [ ] Confirm database name matches

### Health Check Fails
- [ ] Wait 2-3 minutes for full startup
- [ ] Check `/actuator/health` endpoint manually
- [ ] Review application logs for startup errors

## Maintenance

### Regular Tasks
- [ ] Monitor application logs weekly
- [ ] Check for security updates monthly
- [ ] Review MongoDB usage/costs
- [ ] Update dependencies quarterly

### Scaling (when needed)
- [ ] Upgrade Render plan if needed
- [ ] Consider MongoDB Atlas tier upgrade
- [ ] Set up Redis for caching (optional)
- [ ] Configure CDN for static assets (optional)

## Resources

- **Detailed Guide**: See `DEPLOYMENT.md`
- **Quick Reference**: See `QUICK_START.md`
- **Render Docs**: https://render.com/docs
- **MongoDB Docs**: https://docs.atlas.mongodb.com/

## Notes

- Free tier spins down after 15 minutes of inactivity
- First request after spin-down takes 30-60 seconds
- Monitor usage to avoid unexpected charges
- Keep MongoDB credentials secure

---

**Status**: [ ] Not Started  [ ] In Progress  [X] Completed

**Deployed URL**: _____________________________

**Deployment Date**: _____________________________
