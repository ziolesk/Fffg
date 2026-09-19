-- Bei Poa Marketplace Supabase Schema

-- Profiles Table
create table if not exists public.profiles (
  id uuid references auth.users on delete cascade primary key,
  email text,
  name text,
  university text,
  role text default 'student',
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

alter table public.profiles enable row level security;

create policy "Public profiles are viewable by everyone." on public.profiles
  for select using (true);

create policy "Users can insert their own profile." on public.profiles
  for insert with check (auth.uid() = id);

create policy "Users can update own profile." on public.profiles
  for update using (auth.uid() = id);

-- Items Table
create table if not exists public.items (
  id uuid default gen_random_uuid() primary key,
  title text not null,
  price numeric not null,
  category text not null,
  condition text default 'Good',
  university text not null,
  contact text not null,
  image_url text,
  seller_id uuid references public.profiles(id) on delete cascade,
  seller_name text,
  status text default 'approved',
  sold boolean default false,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

alter table public.items enable row level security;

create policy "Items are viewable by everyone." on public.items
  for select using (true);

create policy "Authenticated users can create items." on public.items
  for insert with check (auth.role() = 'authenticated');

create policy "Users can update their own items." on public.items
  for update using (auth.uid() = seller_id);

create policy "Users can delete their own items." on public.items
  for delete using (auth.uid() = seller_id);
