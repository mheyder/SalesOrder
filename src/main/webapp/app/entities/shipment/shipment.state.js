(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('shipment', {
            parent: 'entity',
            url: '/shipment?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Shipments'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/shipment/shipments.html',
                    controller: 'ShipmentController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('shipment-detail', {
            parent: 'entity',
            url: '/shipment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Shipment'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/shipment/shipment-detail.html',
                    controller: 'ShipmentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Shipment', function($stateParams, Shipment) {
                    return Shipment.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'shipment',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('shipment-detail.edit', {
            parent: 'shipment-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipment/shipment-dialog.html',
                    controller: 'ShipmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Shipment', function(Shipment) {
                            return Shipment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('shipment.new', {
            parent: 'shipment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipment/shipment-dialog.html',
                    controller: 'ShipmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                note: null,
                                status: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('shipment', null, { reload: 'shipment' });
                }, function() {
                    $state.go('shipment');
                });
            }]
        })
        .state('shipment.edit', {
            parent: 'shipment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipment/shipment-dialog.html',
                    controller: 'ShipmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Shipment', function(Shipment) {
                            return Shipment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('shipment', null, { reload: 'shipment' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('shipment.delete', {
            parent: 'shipment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipment/shipment-delete-dialog.html',
                    controller: 'ShipmentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Shipment', function(Shipment) {
                            return Shipment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('shipment', null, { reload: 'shipment' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
