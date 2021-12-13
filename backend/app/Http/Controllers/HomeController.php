<?php

namespace App\Http\Controllers;

use App\Bracelet;
use App\BraceletLog;
use Illuminate\Http\Request;

class HomeController extends Controller
{
    public function index()
    {
        $total = Bracelet::count();
        $inside = Bracelet::where('last_action', 'in')->count();
        $outside = Bracelet::where('last_action', 'out')->count();

        return view('welcome', compact('total', 'inside', 'outside'));
    }
}
