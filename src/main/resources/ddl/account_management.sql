PGDMP                  	        w            postgres    11.2    11.2     7           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            8           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            9           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false            :           1262    13329    postgres    DATABASE     f   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'C';
    DROP DATABASE postgres;
             postgres    false            ;           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                  postgres    false    3130            �            1259    16393    account_management    TABLE     h   CREATE TABLE public.account_management (
    id bigint NOT NULL,
    name character varying NOT NULL
);
 &   DROP TABLE public.account_management;
       public         postgres    false            4          0    16393    account_management 
   TABLE DATA               6   COPY public.account_management (id, name) FROM stdin;
    public       postgres    false    197          �           2606    16400    account_management account_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY public.account_management
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);
 I   ALTER TABLE ONLY public.account_management DROP CONSTRAINT account_pkey;
       public         postgres    false    197            4      x�3�L)�ͭ4�2�0��b���� O-�     