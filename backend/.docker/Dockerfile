FROM php:7.4-apache

USER root
WORKDIR /var/www/html

COPY php.ini "$PHP_INI_DIR/php.ini"

RUN apt-get update && apt-get install -y \
        libpng-dev \
        zlib1g-dev \
        libxml2-dev \
        libzip-dev \
        libonig-dev \
        zip \
        curl \
        unzip \
        openssl \
        git \
        nano \
        wget \
    && docker-php-ext-configure gd \
    && docker-php-ext-install -j$(nproc) gd \
    && docker-php-ext-install pdo_mysql mysqli zip soap bcmath \
    && docker-php-source delete

#Instalar composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

COPY vhost.conf /etc/apache2/sites-available/000-default.conf
COPY entrypoint.sh /entrypoint.sh

RUN a2enmod rewrite
RUN service apache2 restart

RUN chmod +x /entrypoint.sh

ENTRYPOINT /entrypoint.sh

