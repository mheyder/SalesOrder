(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('order-item', {
            parent: 'entity',
            url: '/order-item?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'OrderItems'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/order-item/order-items.html',
                    controller: 'OrderItemController',
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
                }]
            }
        })
        .state('order-item-detail', {
            parent: 'entity',
            url: '/order-item/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'OrderItem'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/order-item/order-item-detail.html',
                    controller: 'OrderItemDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'OrderItem', function($stateParams, OrderItem) {
                    return OrderItem.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'order-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('order-item-detail.edit', {
            parent: 'order-item-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/order-item/order-item-dialog.html',
                    controller: 'OrderItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OrderItem', function(OrderItem) {
                            return OrderItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('order-item.new', {
            parent: 'order-item',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/order-item/order-item-dialog.html',
                    controller: 'OrderItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                quantity: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('order-item', null, { reload: 'order-item' });
                }, function() {
                    $state.go('order-item');
                });
            }]
        })
        .state('order-item.edit', {
            parent: 'order-item',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/order-item/order-item-dialog.html',
                    controller: 'OrderItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OrderItem', function(OrderItem) {
                            return OrderItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('order-item', null, { reload: 'order-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('order-item.delete', {
            parent: 'order-item',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/order-item/order-item-delete-dialog.html',
                    controller: 'OrderItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OrderItem', function(OrderItem) {
                            return OrderItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('order-item', null, { reload: 'order-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
