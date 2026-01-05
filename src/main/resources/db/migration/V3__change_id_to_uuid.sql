-- Migration to change user IDs from BIGINT to VARCHAR(36) for UUID storage

-- Drop all foreign key constraints on follows table (including auto-generated ones)
DO $$
DECLARE
    constraint_name text;
BEGIN
    FOR constraint_name IN
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'user_service.follows'::regclass
        AND contype = 'f'
    LOOP
        EXECUTE format('ALTER TABLE user_service.follows DROP CONSTRAINT %I', constraint_name);
    END LOOP;
END $$;

-- Drop the IDENTITY property from the id column
ALTER TABLE user_service.users ALTER COLUMN id DROP IDENTITY IF EXISTS;

-- Change user ID column type
ALTER TABLE user_service.users ALTER COLUMN id TYPE VARCHAR(255);

-- Change follower and following ID column types in follows table
ALTER TABLE user_service.follows ALTER COLUMN follower_id TYPE VARCHAR(255);
ALTER TABLE user_service.follows ALTER COLUMN following_id TYPE VARCHAR(255);

-- Restore foreign key constraints
ALTER TABLE user_service.follows 
    ADD CONSTRAINT follows_follower_id_fkey 
    FOREIGN KEY (follower_id) REFERENCES user_service.users(id) ON DELETE CASCADE;

ALTER TABLE user_service.follows 
    ADD CONSTRAINT follows_following_id_fkey 
    FOREIGN KEY (following_id) REFERENCES user_service.users(id) ON DELETE CASCADE;
