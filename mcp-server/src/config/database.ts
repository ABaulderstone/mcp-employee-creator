import mysql from 'mysql2/promise';
import dotenv from 'dotenv';

dotenv.config();

console.log(process.env.DB_USER);
console.log(process.env.DB_PASSWORD);
export const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || '',
  port: parseInt(process.env.DB_PORT || '3306'),
};

let pool: mysql.Pool;

export const getPool = (): mysql.Pool => {
  if (!pool) {
    pool = mysql.createPool({
      ...dbConfig,
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0,
    });
  }
  return pool;
};
