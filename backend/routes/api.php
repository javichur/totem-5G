<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/



Route::middleware(['CheckApiKey'])->group(function () {
    Route::post('/readNfc', [App\Http\Controllers\Api\ReadNfcController::class, 'read']);
    Route::get('/readNfc', [App\Http\Controllers\Api\ReadNfcController::class, 'read']);
});

