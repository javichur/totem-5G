<?php

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/


Route::get('/', [App\Http\Controllers\HomeController::class, 'index']);

Route::get('/bracelets', [App\Http\Controllers\BraceletController::class, 'index']);
Route::get('/bracelet/create', [App\Http\Controllers\BraceletController::class, 'create']);
Route::post('/bracelet/store', [App\Http\Controllers\BraceletController::class, 'store']);
Route::get('/bracelet/{bracelet}/edit', [App\Http\Controllers\BraceletController::class, 'edit']);
Route::put('/bracelet/{bracelet}/update', [App\Http\Controllers\BraceletController::class, 'update']);
Route::delete('/bracelet/{bracelet}/delete', [App\Http\Controllers\BraceletController::class, 'delete']);

Route::get('/bracelet/{bracelet}/logs', [App\Http\Controllers\BraceletController::class, 'logs']);
Route::get('/general-logs', [App\Http\Controllers\BraceletController::class, 'allLogs']);

Route::get('/statusBracelet', [App\Http\Controllers\BraceletController::class, 'status'])->name('bracelet.status');
Route::get('/activateBracelet', [App\Http\Controllers\BraceletController::class, 'activate'])->name('bracelet.activate');


