# Bei Poa – Complete Setup Guide

## 1. Supabase database

### A. Original schema (if not done)
Run `bei-poa-supabase-setup.sql` in **SQL Editor**.

### B. v2 tables (required for chat, favorites, notifications)
Run `sql/upgrade-v2.sql` in **SQL Editor**.

### C. Enable Realtime
**Database → Publications → supabase_realtime**  
Add tables:
- `messages`
- `conversations`
- `notifications`
- `items` (if not already)

### D. Auth URLs
**Authentication → URL Configuration**
- Site URL: `https://YOUR-APP.onrender.com`
- Redirect URLs: `https://YOUR-APP.onrender.com/**`

### E. Email (optional but recommended)
**Authentication → SMTP** with Brevo:
- Host: `smtp-relay.brevo.com`
- Port: `587`
- Username: your Brevo email
- Password: **SMTP key** (not login password)
- Sender email: same as username
- Sender name: `Bei Poa`

**Authentication → Providers → Email → Confirm email** ON if you want verification.

### F. Google login (optional)
**Authentication → Providers → Google**  
Client ID + Secret from Google Cloud Console.  
Redirect URI: `https://mnlfiiuqlacjlaiszygr.supabase.co/auth/v1/callback`

### G. Make yourself admin
**SQL Editor:**
```sql
UPDATE profiles SET role = 'admin' WHERE email = 'your@email.com';
```

---

## 2. GitHub

1. Create a new repository.
2. Upload **all** of these (keep folder structure):

```
package.json
server.js
render.yaml
README.md
SETUP.md
bei-poa-supabase-setup.sql
sql/upgrade-v2.sql
public/index.html
public/css/app.css
public/js/config.js
```

---

## 3. Render Web Service

1. [render.com](https://render.com) → **New** → **Web Service**
2. Connect your GitHub repo
3. Settings:
   - **Name:** bei-poa
   - **Runtime:** Node
   - **Build Command:** `npm install`
   - **Start Command:** `npm start`
   - **Instance:** Free
4. **Create Web Service** → wait for deploy
5. Copy the URL (e.g. `https://bei-poa.onrender.com`)
6. Put that URL back into Supabase **Site URL** + **Redirect URLs**

---

## 4. Test checklist

| Test | How |
|------|-----|
| Register / Login | Email or Google |
| Dark mode | Moon/sun button in header |
| Browse marketplace | After login |
| Sell item | Sell button → pending until admin approves |
| Admin | Login as admin → Admin panel |
| Message seller | Product page → **Message** |
| Favorites | Product page → **Save** |
| WhatsApp | Still available on product page |
| Mobile | Bottom nav + no horizontal zoom |

---

## 5. If chat says run upgrade-v2.sql

You skipped step 1B. Run the SQL, then enable Realtime for `messages`.

---

## 6. Security notes

- The **anon/publishable** key in the frontend is normal for Supabase.
- Never put the **service_role / secret** key in HTML or GitHub.
- RLS policies protect data per user.

---

## 7. Local development

```bash
cd bei-poa
npm install
npm start
```
Open http://localhost:3000
