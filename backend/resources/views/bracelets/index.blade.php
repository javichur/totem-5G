@extends('layouts.app')

@section('content')
    @include('layouts.parts.breadcrumbs', ['title' => 'Bracelets', 'pages' =>[]])

    <div class="row">
            <div class="col-1">
                <a href="{{ url('bracelet/create') }}" class="btn btn-block btn-primary">@lang('Create bracelet')</a>
            </div>

        <br>
        <br>
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"></h3>
                    @include('layouts.parts.form-search')
                </div>
                <!-- /.card-header -->
                <div class="card-body table-responsive p-0">
                    <table class="table table-hover text-nowrap">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">@lang('Name')</th>
                            <th scope="col">@lang('Bracelet UID')</th>
                            <th scope="col">@lang('Last action')</th>
                            <th scope="col">@lang('Secret url')</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        @if(count($bracelets) > 0)
                            @foreach($bracelets as $b)
                                <tr>
                                    <th scope="row">{{$b->id}}</th>
                                    <td>{{$b->name}}</td>
                                    <td>{{$b->uid}}</td>
                                    <td><span class="badge @if($b->last_action && $b->last_action == 'in') badge-success @else badge-danger @endif">{{$b->last_action}}</span></td>
                                    <td><a href="{{$b->signed_url}}" target="_blank">Secret URL</a></td>
                                    <td>
                                        <a href="{{url('/bracelet/'.$b->id.'/logs')}}" class="btn btn-outline-info">
                                            <i class="fas fa-list"></i>
                                        </a>
                                        <a href="{{url('/bracelet/'.$b->id.'/edit')}}" class="btn btn-outline-info">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button style="cursor:pointer;" onclick="confirmForm('delete-bracelet-{{$b->id}}')" class="btn btn-outline-danger">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                        <form action="{{url('/bracelet/'.$b->id.'/delete')}}" method="POST" id="delete-bracelet-{{$b->id}}">
                                            <input type="hidden" name="_method" value="DELETE">
                                            <input type="hidden" name="_token" value="{{ csrf_token() }}">
                                        </form>
                                    </td>
                                </tr>
                            @endforeach
                        @else
                            <tr>
                                <td colspan="10" class="text-center alert-warning">@lang('No bracelets')</td>
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

@push('scripts')
    <script>
        function confirmForm(form_id){

            Swal.fire({
                title: "@lang('Are you sure?')",
                text: "",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "@lang('Yes, delete!')",
            }).then(function (sweetAlertResponse) {
                if(sweetAlertResponse.value){
                    document.getElementById(form_id).submit();
                }
            });

        }
    </script>
@endpush

