<?php

namespace App\Http\Middleware;

use Closure;

class CheckApiKeyMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    const API_TOKEN = '889e88003d36d012f56a860e3180a5235';

    public function handle($request, Closure $next)
    {
        if($request->headers->get("Auth-Token") == self::API_TOKEN) {
            return $next($request);
        }
        return response()->json(["error" => true, "message" => 'Unauthorized'], 401);
    }
}
