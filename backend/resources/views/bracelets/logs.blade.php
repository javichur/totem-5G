@extends('layouts.app')

@section('content')
    @include('layouts.parts.breadcrumbs', ['title' => 'logs', 'pages' =>[]])

    <div class="row">

        <br>
        <div class="col-12">
            <div class="card">
                <!-- /.card-header -->
                <div class="card-body table-responsive p-0">
                    <table class="table table-hover text-nowrap">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">@lang('Action')</th>
                            <th scope="col">@lang('Date time')</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        @if(count($logs) > 0)
                            @foreach($logs as $l)
                                <tr>
                                    <th scope="row">{{$l->id}}</th>
                                    <td><span class="badge @if($l->action == 'in') badge-success @else badge-danger @endif">{{$l->action}}</span></td>
                                    <td>{{$l->date_time}}</td>
                                </tr>
                            @endforeach
                        @else
                            <tr>
                                <td colspan="10" class="text-center alert-warning">@lang('No logs')</td>
                            </tr>
                        @endif
                        </tbody>
                    </table>
                </div>
                <!-- /.card-body -->
            </div>
            <!-- /.card -->
        </div>
    </div>
@endsection



