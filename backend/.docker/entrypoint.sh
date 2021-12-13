#!/bin/bash
echo 'Initializing .docker web container'

composer install --dev
php artisan storage:link
chmod -R 777 storage
chmod -R 777 bootstrap/cache

echo 'Laravel server is running...'
exec /usr/sbin/apachectl -DFOREGROUND
